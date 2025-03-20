package com.example.fooddelivery.data

import retrofit2.http.GET

interface FoodApi {
    @GET
    suspend fun getFoods():List<String>
}