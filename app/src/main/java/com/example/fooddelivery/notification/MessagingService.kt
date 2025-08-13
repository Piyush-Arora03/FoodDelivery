package com.example.fooddelivery.notification

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.example.fooddelivery.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService @Inject constructor() : FirebaseMessagingService() {
    @Inject
    lateinit var notificationManager: NotificationManager
    private val job = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        notificationManager.updateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        job.launch {
            NotificationEvent.emit()
        }
        val intent = Intent(this, MainActivity::class.java)
        val title = message.notification?.title ?: "This is Title"
        val body = message.notification?.body ?: "This is body"
        val data = message.data
        Log.d("Notification", "message-> ${message.data.toString()}")
        val type = data["type"] ?: "general"
        if (type == "order") {
            val orderId = data[OrderId] ?: ""
            intent.putExtra(OrderId, orderId)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationType = when (type) {
            "order" -> NotificationManager.NotificationType.ORDER
            "general" -> NotificationManager.NotificationType.PROMOTION
            else -> NotificationManager.NotificationType.ACCOUNT
        }
        val notificationId = message.data["notificationId"]
        val notificationIdInt = notificationId?.toIntOrNull() ?: notificationId?.hashCode() ?: System.currentTimeMillis().toInt()
        notificationManager.showNotification(title, body, notificationType, pendingIntent, notificationIdInt)
    }

    companion object {
        const val OrderId = "orderId"
    }
}
