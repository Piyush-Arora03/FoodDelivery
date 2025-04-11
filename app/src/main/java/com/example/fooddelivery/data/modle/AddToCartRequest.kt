package com.example.fooddelivery.data.modle

data class AddToCartRequest(
    val restaurantId: String,
    val menuItemId: String,
    val quantity: Int,
) {
}