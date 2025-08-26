package com.example.fooddelivery.data.repository

import com.example.fooddelivery.SocketServiceImpl
import com.example.fooddelivery.data.SocketService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationUpdateSocketRepository @Inject constructor(
    private val socketService: SocketService
) {

    private val _socketConnection = MutableStateFlow<SocketConnection>(SocketConnection.Disconnected)
    val socketConnection = _socketConnection.asStateFlow()

    val messages = socketService.messages

    fun connect(orderId:String, riderId:String){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val currentLocation=getUserLocation()
                socketService.connect(orderId, riderId,currentLocation.latitude,currentLocation.longitude)
                _socketConnection.value = SocketConnection.Connected
            } catch (e: Exception){
                _socketConnection.value = SocketConnection.Disconnected
                e.printStackTrace()
            }
        }
    }
    fun disconnect(){
        try {
            socketService.disconnect()
            _socketConnection.value = SocketConnection.Disconnected
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun sendMessage(message:String){
        socketService.sendMessage(message)
    }

    suspend fun getUserLocation(): LatLng{
        return LatLng(0.0,0.0)
    }
}

sealed class SocketConnection{
    object Connected:SocketConnection()
    object Disconnected:SocketConnection()
}