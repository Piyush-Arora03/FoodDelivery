package com.example.fooddelivery.data.modle

data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String,
    val paymentMethodId: String? = null
)