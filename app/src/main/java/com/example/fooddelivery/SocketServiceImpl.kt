package com.example.fooddelivery

import com.example.fooddelivery.data.SocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketServiceImpl: SocketService {
    private var webSocket: WebSocket?=null

    companion object{
        private const val BASE_URL="ws://192.168.29.117:8080"
    }

    fun createUrl(
        orderId:String,
        riderId:String,
        latitude:Double,
        longitude:Double,
        type:String="LOCATION UPDATE"
    ):String{
        return "${BASE_URL}/track/$orderId?riderId=$riderId&latitude=$latitude&longitude=$longitude&type=$type"
    }

    override fun connect(orderId: String, riderId: String, latitude: Double, longitude: Double) {
        val builder = Request.Builder().url(createUrl(orderId,riderId,latitude,longitude)).build()
        val client = OkHttpClient.Builder().build()
        webSocket = client.newWebSocket(builder,createWebSocketListener())
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener(){
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                CoroutineScope(Dispatchers.IO).launch {
                    _messages.emit(text)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
            }
        }
    }

    override fun disconnect() {
        webSocket?.close(1000,"GoodBye")
        webSocket=null
    }

    override fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    private val _messages=MutableStateFlow<String>("")
    override val messages: Flow<String>
        get() = _messages.asStateFlow()
}