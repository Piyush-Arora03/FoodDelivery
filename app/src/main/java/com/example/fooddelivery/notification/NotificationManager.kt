package com.example.fooddelivery.notification

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.FCMTokenRequest
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NotificationManager @Inject constructor(val foodApi: FoodApi,@ApplicationContext val context: Context) {
    val notificationManager = NotificationManagerCompat.from(context)
    val job= CoroutineScope(Dispatchers.IO+SupervisorJob())
    enum class NotificationType(
        val id: String,
        val channelName: String,
        val channelDescription: String,
        val importance: Int
    ) {
        ORDER("1", "Order", "Order Description", NotificationManager.IMPORTANCE_HIGH),
        ACCOUNT("2", "Account", "Account Description", NotificationManager.IMPORTANCE_HIGH),
        PROMOTION("3", "Promotion", "Promotion Description", NotificationManager.IMPORTANCE_DEFAULT),
    }


    fun createNotificationChannel() {
        NotificationType.values().forEach {
            val channel= NotificationChannelCompat.Builder(it.id, it.importance)
                .setName(it.channelName)
                .setDescription(it.channelDescription)
                .build()
                notificationManager.createNotificationChannel(channel)
        }
    }

    fun getAndUpdateToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { token ->
                    updateToken(token)
                }
            }
        }
    }

    fun updateToken(token: String) {
        job.launch {
            val res= SafeApiCalls {
                foodApi.updateToken(FCMTokenRequest(token))
            }
        }
    }

    fun showNotification(
        title: String,
        body: String,
        notificationType: NotificationType,
        intent: PendingIntent,
        notificationId: Int
    ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        val notification= NotificationCompat.Builder(context,notificationType.id)
            .setContentText(body)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentIntent(intent)
            .setPriority(notificationType.importance)
            .build()

        notificationManager.notify(notificationId,notification)
    }
}