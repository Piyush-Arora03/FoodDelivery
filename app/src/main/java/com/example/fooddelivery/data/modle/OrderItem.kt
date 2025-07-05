package com.example.fooddelivery.data.modle

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val menuItemName: String,
    val orderId: String,
    val quantity: Int
)