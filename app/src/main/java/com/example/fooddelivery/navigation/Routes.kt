package com.example.fooddelivery.navigation

import com.example.fooddelivery.data.modle.FoodItem
import kotlinx.serialization.Serializable

interface NavRoutes
@Serializable
object SignUpScreen: NavRoutes

@Serializable
object LogInScreen: NavRoutes

@Serializable
object AuthScreen: NavRoutes

@Serializable
object HomeScreen: NavRoutes

@Serializable
data class RestaurantDetailScreen(val name:String,val imageUrl:String,val id: String): NavRoutes

@Serializable
data class FoodDetailScreen(val foodItem:FoodItem): NavRoutes

@Serializable
object CartScreen: NavRoutes

@Serializable
object NotificationScreen: NavRoutes
