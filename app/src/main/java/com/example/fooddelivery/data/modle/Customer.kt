package com.example.fooddelivery.data.modle

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val addressLine1: String,
    val addressLine2: String?=null,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val zipCode: String
)