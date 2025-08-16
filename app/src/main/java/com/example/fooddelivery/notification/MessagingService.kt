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

    // ... (imports and class definition are the same) ...

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        job.launch {
            NotificationEvent.emit()
        }

        // --- START OF FIX ---

        // 1. Create the Intent for your MainActivity.
        val intent = Intent(this, MainActivity::class.java).apply {
            // 2. ADD THESE FLAGS. This is the crucial part.
            //    - FLAG_ACTIVITY_CLEAR_TOP: If MainActivity is already running, this will clear all other activities on top of it.
            //    - FLAG_ACTIVITY_SINGLE_TOP: This ensures that if MainActivity is already at the top, a new instance won't be created.
            //      Instead, its onNewIntent() method will be called.
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("from_notification", true)
        }

        // --- END OF FIX ---

        val title = message.notification?.title ?: "This is Title"
        val body = message.notification?.body ?: "This is body"
        val data = message.data
        Log.d("Notification", "message-> ${message.data.toString()}")
        val type = data["type"] ?: "general"
        if (type == "order") {
            val orderId = data[OrderId] ?: ""
            intent.putExtra(OrderId, orderId) // Your extra is added to the configured intent
        }

        // This line is now correct because the 'intent' it uses has the right flags.
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

// ... (companion object is the same) ...

    companion object {
        const val OrderId = "orderId"
    }
}
