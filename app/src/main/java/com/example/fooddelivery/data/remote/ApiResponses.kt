package com.example.fooddelivery.data.remote

import com.google.android.gms.common.api.Api
import retrofit2.Response

sealed class ApiResponses<T> {
    data class Success<T>(val data: T) : ApiResponses<T>()
    data class Error<T>(val code: Int,val msg:String) : ApiResponses<T>()
    data class Exception<T>(val exception: String) : ApiResponses<T>()
}

suspend fun <T> SafeApiCalls(apiCall: suspend () -> Response<T>): ApiResponses<T>{
    return try{
        val res=apiCall.invoke()
        if(res.isSuccessful){
            ApiResponses.Success(res.body()!!)
        }else{
            ApiResponses.Error(res.code(),res.message())
        }
    }catch (e:Exception){
            ApiResponses.Exception(e.message.toString())
    }
}