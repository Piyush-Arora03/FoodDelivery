package com.example.fooddelivery.utils

class OrderStatusUtils {
    enum class OrderStatus {
        PENDING_ACCEPTANCE, // Initial state when order is placed
        ACCEPTED,          // Restaurant accepted the order
        PREPARING,         // Food is being prepared
        READY,            // Ready for delivery/pickup
        ASSIGNED,
        DELIVERED,        // Order completed
        DELIVERY_FAILED,        // Order completed
        REJECTED,         // Restaurant rejected the order
        CANCELLED         // Customer cancelled
    }
}