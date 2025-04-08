package com.example.fooddelivery.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideRetrofitClient(session: FoodHubAuthSession):Retrofit{
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor {chain->
                val request=chain.request().newBuilder()
                    .addHeader("Authorization","Bearer ${session.getToken()}")
                    .build()
                chain.proceed(request)
            }
            .build()
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
}