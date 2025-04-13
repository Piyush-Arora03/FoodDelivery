package com.example.fooddelivery.data.modle

data class PaymentIntentResponse(
    val paymentIntentClientSecret: String,
    val paymentIntentId: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val publishableKey: String,
    val amount: Long,
    val currency: String = "usd",
    val status: String
)