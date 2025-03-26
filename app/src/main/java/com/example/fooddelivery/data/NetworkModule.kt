package com.example.fooddelivery.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideRetrofitClient():Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    fun provideFoodApi(retrofit: Retrofit):FoodApi{
        return retrofit.create(FoodApi::class.java)
    }
}