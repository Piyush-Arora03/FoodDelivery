package com.example.fooddelivery.utils

import kotlinx.coroutines.flow.MutableStateFlow

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    object Loading : UiState<Nothing>
    object Empty: UiState<Nothing>
}

fun <T> MutableStateFlow<UiState<T>>.toSuccess(data: T) {
    value = UiState.Success(data)
}

fun <T> MutableStateFlow<UiState<T>>.toError(message: String) {
    value = UiState.Error(message)
}

fun <T> MutableStateFlow<UiState<T>>.toLoading() {
    value = UiState.Loading
}

fun <T> MutableStateFlow<UiState<T>>.toResetUi() {
        value = UiState.Empty
}
