package com.example.fooddelivery.data

import com.example.fooddelivery.data.modle.AuthResponse
import com.example.fooddelivery.data.modle.OAuthRequest
import com.example.fooddelivery.data.modle.SignInRequest
import com.example.fooddelivery.data.modle.SignUpRequest
import okhttp3.Request
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET
    suspend fun getFoods():List<String>
    @POST("/auth/signup")
    suspend fun signUpRequest(@Body signUpRequest: SignUpRequest) : AuthResponse
    @POST("/auth/login")
    suspend fun signInRequest(@Body signUpRequest: SignInRequest): AuthResponse
    @POST("/auth/oauth")
    suspend fun oAuthRequest(@Body oAuthRequest: OAuthRequest): AuthResponse
}