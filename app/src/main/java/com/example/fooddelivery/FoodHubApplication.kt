package com.example.fooddelivery

import android.app.Application
import com.example.fooddelivery.notification.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodHubApplication:Application(){
    @Inject
    lateinit var notificationManager:NotificationManager
    override fun onCreate() {
        super.onCreate()
        notificationManager.createNotificationChannel()
        notificationManager.getAndUpdateToken()
    }
}