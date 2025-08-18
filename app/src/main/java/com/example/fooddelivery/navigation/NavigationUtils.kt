package com.example.fooddelivery.navigation

import android.content.Intent
import androidx.navigation.NavController
import com.example.fooddelivery.notification.MessagingService

object NavigationUtils {

    fun handleIntent(intent: Intent?, navController: NavController) {
        if (intent?.hasExtra(MessagingService.OrderId) == true) {
            val orderId = intent.getStringExtra(MessagingService.OrderId)
            if (orderId != null) {
                navController.navigate(OrderDetailScreen(orderId = orderId))
            }
            intent.removeExtra(MessagingService.OrderId)
        }
    }
}
