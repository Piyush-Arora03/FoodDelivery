package com.example.fooddelivery.data.modle

data class RiderDeliveryData(
    val createdAt: String,
    val customer: Customer,
    val estimatedEarning: Double,
    val items: List<RiderDeliveryItem>,
    val orderId: String,
    val restaurant: Restaurant,
    val status: String,
    val totalAmount: Double,
    val updatedAt: String
)