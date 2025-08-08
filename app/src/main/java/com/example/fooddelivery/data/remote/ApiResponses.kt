package com.example.fooddelivery.data.remote

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class ApiResponses<T> {
    data class Success<T>(val data: T) : ApiResponses<T>()
    data class Error<T>(val code: Int, val msg: String) : ApiResponses<T>()
    data class Exception<T>(val exception: Throwable) : ApiResponses<T>()
}

suspend fun <T> SafeApiCalls(apiCall: suspend () -> Response<T>): ApiResponses<T> {
    return try {
        val res = apiCall.invoke()
        if (res.isSuccessful) {
            ApiResponses.Success(res.body()!!)
        } else {
            ApiResponses.Error(res.code(), res.errorBody()?.string() ?: res.message())
        }
    } catch (e: HttpException) {
        ApiResponses.Exception(e)
    } catch (e: IOException) {
        ApiResponses.Exception(e)
    } catch (e: Exception) {
        ApiResponses.Exception(e)
    }
}