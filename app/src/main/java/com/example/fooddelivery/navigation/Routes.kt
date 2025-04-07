package com.example.fooddelivery.navigation

import com.example.fooddelivery.data.modle.FoodItem
import kotlinx.serialization.Serializable

@Serializable
object SignUpScreen

@Serializable
object LogInScreen

@Serializable
object AuthScreen

@Serializable
object HomeScreen

@Serializable
data class RestaurantDetail(val name:String,val imageUrl:String,val id: String)

@Serializable
data class FoodDetailScreen(val foodItem:FoodItem)
