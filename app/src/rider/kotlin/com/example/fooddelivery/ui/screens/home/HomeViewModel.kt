package com.example.fooddelivery.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.RiderAvailableDeliveriesResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
import com.example.fooddelivery.utils.toEmpty
import com.example.fooddelivery.utils.toError
import com.example.fooddelivery.utils.toLoading
import com.example.fooddelivery.utils.toSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
    private val _uiState= MutableStateFlow<UiState<RiderHomeState>>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _decisionResponse= MutableSharedFlow<String>()
    val decisionResponse= _decisionResponse.asSharedFlow()

    data class RiderHomeState(
        val deliveryList:RiderAvailableDeliveriesResponse?=null,
        val msg: GenericMsgResponse?=null
    )

    init {
        getAvailableDeliveries()
    }

    fun getAvailableDeliveries(){
        viewModelScope.launch {
        _uiState.toLoading()
            SafeApiCalls { foodApi.getAvailableDeliveries() }
                .let {
                    when(it){
                        is ApiResponses.Error<*> -> {
                            _uiState.toError(it.msg)
                        }
                        is ApiResponses.Exception<*> -> {
                            handleException(it.exception,_uiState)
                        }
                        is ApiResponses.Success<*> -> {
                            val currentState=(uiState.value as? UiState.Success)?.data ?: RiderHomeState()
                            val data=currentState.copy(deliveryList = it.data as RiderAvailableDeliveriesResponse)
                            _uiState.toSuccess(data)
                        }
                    }
                }
        }
    }

    fun onAccept(orderId:String){
        val currentState=(uiState.value as? UiState.Success)?.data
        _uiState.toLoading()
        val data=currentState?.deliveryList?.data
        val optimisticList=data?.filterNot { it.orderId==orderId }
        val response=currentState?.deliveryList?.copy(data = optimisticList!!)
        val optimisticData=currentState?.copy(deliveryList = response)
        _uiState.toSuccess(optimisticData!!)
        viewModelScope.launch {
            SafeApiCalls { foodApi.onDeliveryAccept(orderId) }.let {
                when(it){
                    is ApiResponses.Error<*> -> {
                        _uiState.toError(it.msg)
                        _decisionResponse.emit("Failed to accept order. Please try again.")
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception,_uiState)
                        _decisionResponse.emit("An error occurred.")
                    }
                    is ApiResponses.Success<*> -> {
                        _decisionResponse.emit("Order Accepted!")
                    }
                }
            }
        }
    }

    fun onDecline(orderId:String){
        val currentState=(uiState.value as? UiState.Success)?.data
        _uiState.toLoading()
        val data=currentState?.deliveryList?.data
        val optimisticList=data?.filterNot { it.orderId==orderId }
        val response=currentState?.deliveryList?.copy(data = optimisticList!!)
        val optimisticData=currentState?.copy(deliveryList = response)
        _uiState.toSuccess(optimisticData!!)
        viewModelScope.launch {
            SafeApiCalls { foodApi.onDeliveryReject(orderId) }.let {
                when(it){
                    is ApiResponses.Error<*> -> {
                        _uiState.toError(it.msg)
                        _decisionResponse.emit("Failed to accept order. Please try again.")
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception,_uiState)
                        _decisionResponse.emit("An error occurred.")
                    }
                    is ApiResponses.Success<*> -> {
                        _decisionResponse.emit("Order Declined")
                    }
                }
            }
        }
    }
    fun resetUi(){
        getAvailableDeliveries()
    }
    fun emptyState(){
        _uiState.toEmpty()
    }
}