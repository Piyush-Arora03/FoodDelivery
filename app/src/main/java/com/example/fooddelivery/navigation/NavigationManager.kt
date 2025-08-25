package com.example.fooddelivery.navigation

import android.content.Intent
import com.example.fooddelivery.notification.MessagingService
import kotlinx.coroutines.flow.MutableStateFlow

sealed class NavigationEvent {
    data class NavigateToOrderDetail(val orderId: String) : NavigationEvent()
    object None : NavigationEvent()
}

object NavigationManager {

    val navigationEvent = MutableStateFlow<NavigationEvent>(NavigationEvent.None)

    fun processIntent(intent: Intent?) {
        if (intent?.hasExtra(MessagingService.OrderId) == true) {
            val orderId = intent.getStringExtra(MessagingService.OrderId)
            if (orderId != null) {
                navigationEvent.value = NavigationEvent.NavigateToOrderDetail(orderId)
            }
            intent.removeExtra(MessagingService.OrderId)
        }
    }

    fun clearEvent() {
        navigationEvent.value = NavigationEvent.None
    }
}
