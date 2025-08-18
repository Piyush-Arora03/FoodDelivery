package com.example.fooddelivery.ui.screens.order_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.modle.OrderListResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.OrderStatusUtils
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
import com.example.fooddelivery.utils.toEmpty
import com.example.fooddelivery.utils.toError
import com.example.fooddelivery.utils.toSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(val foodApi: FoodApi): ViewModel(){
    data class OrderDetailState(
        val orders: OrderListResponse? = null,
    )

    private val _uiState= MutableStateFlow<UiState<OrderDetailState>>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    fun getOrdersType():List<String> {
        val type= OrderStatusUtils.OrderStatus.entries.map { it.name }
        return type
    }

    fun getRestaurantOrderDetails(status:String){
        viewModelScope.launch {
            SafeApiCalls { foodApi.getRestaurantOrderDetails(status) }.let {
                when(it){
                    is ApiResponses.Error<*> -> {
                        _uiState.toError(it.msg)
                        Log.d("OrderDetailViewModel",it.msg)
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception, _uiState)
                        Log.d("OrderDetailViewModel",it.exception.toString())
                    }
                    is ApiResponses.Success<*> -> {
                        val currentState = (_uiState.value as? UiState.Success)?.data ?: OrderDetailState()
                        _uiState.toSuccess(currentState.copy(orders = it.data as OrderListResponse))
                        Log.d("OrderDetailViewModel",it.data.toString())
                    }
                }
            }
        }
    }

    fun resetUi(status: String){
        getRestaurantOrderDetails(status)
    }

    fun emptyState(){
        _uiState.toEmpty()
    }

}