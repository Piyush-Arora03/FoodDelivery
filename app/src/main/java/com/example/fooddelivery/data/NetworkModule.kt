package com.example.fooddelivery.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialOption
import com.example.fooddelivery.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideClient(session: FoodHubAuthSession): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${session.getToken()}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build() // return the built OkHttpClient, not the Builder
    }
    @Provides
    fun provideRetrofitClient(client: OkHttpClient):Retrofit{
//        192.168.29.117
//        10.0.2.2
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    @Provides
    fun provideFoodApi(retrofit: Retrofit):FoodApi{
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun foodHubAuthSession(@ApplicationContext context: Context) : FoodHubAuthSession{
        return FoodHubAuthSession(context =context)
    }

    @Provides
    fun getLocation(@ApplicationContext context: Context) : FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(context)
    }
}