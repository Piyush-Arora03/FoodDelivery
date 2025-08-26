package com.example.fooddelivery.data

import kotlinx.coroutines.flow.Flow

interface SocketService {
    fun connect(
        orderId:String,
        riderId:String,
        latitude:Double,
        longitude:Double
    )
    fun disconnect()
    fun sendMessage(message: String)
    val messages: Flow<String>
}