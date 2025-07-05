package com.example.fooddelivery.data.modle

data class ConfirmPaymentResponse(
    val status: String,
    val requiresAction: Boolean,
    val clientSecret: String,
    val orderId: String? = null,
    val orderStatus: String? = null,
    val message: String? = null
)