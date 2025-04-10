package com.example.fooddelivery.data

import com.example.fooddelivery.data.modle.AddToCartRequest
import com.example.fooddelivery.data.modle.AddToCartResponse
import com.example.fooddelivery.data.modle.AuthResponse
import com.example.fooddelivery.data.modle.CartResponse
import com.example.fooddelivery.data.modle.CategoriesResponse
import com.example.fooddelivery.data.modle.FoodItemResponse
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.OAuthRequest
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.data.modle.RestaurantResponse
import com.example.fooddelivery.data.modle.SignInRequest
import com.example.fooddelivery.data.modle.SignUpRequest
import com.example.fooddelivery.data.modle.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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
}