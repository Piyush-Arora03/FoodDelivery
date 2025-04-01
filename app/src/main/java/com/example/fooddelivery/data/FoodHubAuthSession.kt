package com.example.fooddelivery.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class FoodHubAuthSession @Inject constructor(@ApplicationContext val context: Context) {
    val sharedPref : SharedPreferences=context.getSharedPreferences("foodhub", Context.MODE_PRIVATE)
    fun saveToken(token: String?) {
        sharedPref.edit().putString("token", token).apply()
    }
    fun getToken(): String? {
        return sharedPref.getString("token", null)
    }
}