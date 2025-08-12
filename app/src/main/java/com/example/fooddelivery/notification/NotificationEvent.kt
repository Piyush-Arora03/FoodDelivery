package com.example.fooddelivery.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEvent {
    private val _events = MutableSharedFlow<Unit>()
    val events = _events.asSharedFlow()

    suspend fun emit() {
        _events.emit(Unit)
    }
}
