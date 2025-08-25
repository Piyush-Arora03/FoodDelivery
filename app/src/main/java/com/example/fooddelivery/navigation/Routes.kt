package com.example.fooddelivery.navigation

import com.example.fooddelivery.data.modle.Customer
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

@Serializable
object AddressListScreen:NavRoutes

@Serializable
object AddAddressScreen:NavRoutes

@Serializable
data class OrderSuccessScreen(val orderId:String):NavRoutes

@Serializable
data class OrderDetailScreen(val orderId:String):NavRoutes

@Serializable
object OrdersListScreen:NavRoutes

@Serializable
object RestaurantOrdersScreen: NavRoutes

@Serializable
object RestaurantNotificationScreen: NavRoutes

@Serializable
data class RestaurantMenuItem(val restaurantId: String): NavRoutes

@Serializable
object AddMenu: NavRoutes

@Serializable
object RiderDeliveryItemScreen: NavRoutes

@Serializable
data class RiderOrderDetail(val orderId:String): NavRoutes