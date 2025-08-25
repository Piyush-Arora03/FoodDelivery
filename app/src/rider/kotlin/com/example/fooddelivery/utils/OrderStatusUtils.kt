package com.example.fooddelivery.utils

class OrderStatusUtils {
    enum class OrderStatus {
        ASSIGNED,
        OUT_FOR_DELIVERY, // Rider picked up
        DELIVERED,        // Order completed
        DELIVERY_FAILED,        // Order completed
    }
}