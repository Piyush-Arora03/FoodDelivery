package com.example.fooddelivery.data

import com.example.fooddelivery.data.modle.AddToCartRequest
import com.example.fooddelivery.data.modle.AddToCartResponse
import com.example.fooddelivery.data.modle.Address
import com.example.fooddelivery.data.modle.AddressListResponse
import com.example.fooddelivery.data.modle.AuthResponse
import com.example.fooddelivery.data.modle.CartResponse
import com.example.fooddelivery.data.modle.CategoriesResponse
import com.example.fooddelivery.data.modle.ConfirmPaymentRequest
import com.example.fooddelivery.data.modle.ConfirmPaymentResponse
import com.example.fooddelivery.data.modle.FCMTokenRequest
import com.example.fooddelivery.data.modle.FoodItemResponse
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.Notification
import com.example.fooddelivery.data.modle.NotificationResponse
import com.example.fooddelivery.data.modle.OAuthRequest
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.modle.OrderListResponse
import com.example.fooddelivery.data.modle.PaymentIntentRequest
import com.example.fooddelivery.data.modle.PaymentIntentResponse
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.data.modle.RestaurantResponse
import com.example.fooddelivery.data.modle.ReverseGeocodeRequest
import com.example.fooddelivery.data.modle.SignInRequest
import com.example.fooddelivery.data.modle.SignUpRequest
import com.example.fooddelivery.data.modle.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @GET("/categories")
    suspend fun getCategories():Response<CategoriesResponse>


    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double
    ):Response<RestaurantResponse>


    @POST("/auth/signup")
    suspend fun signUpRequest(@Body signUpRequest: SignUpRequest) : Response<AuthResponse>


    @POST("/auth/login")
    suspend fun signInRequest(@Body signUpRequest: SignInRequest): Response<AuthResponse>


    @POST("/auth/oauth")
    suspend fun oAuthRequest(@Body oAuthRequest: OAuthRequest): Response<AuthResponse>

    @GET("/restaurants/{id}/menu")
    suspend fun getFootItem(@Path ("id") id:String):Response<FoodItemResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest):Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCart():Response<CartResponse>

    @PATCH("/cart")
    suspend fun updateQuantity(@Body request: UpdateCartItemRequest):Response<GenericMsgResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun removeItem(@Path ("cartItemId") id:String):Response<GenericMsgResponse>

    @GET("/addresses")
    suspend fun getAddresses():Response<AddressListResponse>

    @POST("/addresses/reverse-geocode")
    suspend fun reverseGeocode(@Body request: ReverseGeocodeRequest):Response<Address>

    @POST("/addresses")
    suspend fun addAddress(@Body request: Address): Response<GenericMsgResponse>

    @POST("/payments/create-intent")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequest):Response<PaymentIntentResponse>

    @POST("/payments/confirm/{paymentIntentId}")
    suspend fun verifyPurchase(@Body request:ConfirmPaymentRequest,@Path("paymentIntentId") paymentIntentId:String): Response<ConfirmPaymentResponse>

    @GET("/orders")
    suspend fun getOrders():Response<OrderListResponse>

    @GET("/orders/{orderId}")
    suspend fun getOrdersDetails(@Path("orderId") orderId:String):Response<Order>

    @PUT("/notifications/fcm-token")
    suspend fun updateToken(@Body token: FCMTokenRequest):Response<GenericMsgResponse>

    @GET("/notifications")
    suspend fun getNotifications():Response<NotificationResponse>

    @POST("/notifications/{id}/read")
    suspend fun readNotification(@Path("id") id:String):Response<GenericMsgResponse>
}