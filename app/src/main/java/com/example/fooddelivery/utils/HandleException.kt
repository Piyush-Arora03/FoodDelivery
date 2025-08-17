package com.example.fooddelivery.utils

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException
import java.io.IOException

fun <T> handleException(exception: Throwable, uiStateFlow: MutableStateFlow<UiState<T>>) {
    when (exception) {
        is HttpException -> {
            uiStateFlow.value = UiState.Error("HTTP Error: ${exception.code()}")
        }

        is IOException -> {
            uiStateFlow.value = UiState.Error("Network Error: Please check your internet connection")
        }

        else -> {
            uiStateFlow.value = UiState.Error("Something went wrong: ${exception.message}")
        }
    }
}
