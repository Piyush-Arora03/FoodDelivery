package com.example.fooddelivery.ui.screens.order_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
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
class OrderDetailViewModel @Inject constructor(
    private val foodApi: FoodApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Order>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<OrderDetailNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    var errMsg=""

    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            _uiState.toLoading()
            when (val response = SafeApiCalls { foodApi.getOrdersDetails(orderId) }) {
                is ApiResponses.Success -> {
                    _uiState.toSuccess(response.data)
                }

                is ApiResponses.Error -> {
                    _uiState.toError(response.msg)
                }

                is ApiResponses.Exception -> {
                    _uiState.toError("Unknown Error")
                    handleException(response.exception, _uiState)
                }
            }
        }
    }

    fun navigateBack(){
        viewModelScope.launch {
            _navigationEvent.emit(OrderDetailNavigationEvent.NavigateBack)
        }
    }

//    fun cancelOrder(orderId: String) {
//        viewModelScope.launch {
//            when (val response = SafeApiCalls { foodApi.cancelOrder(orderId) }) {
//                is ApiResponses.Success -> {
//                    getOrderDetails(orderId)
//                }
//
//                is ApiResponses.Error -> {
//                    errMsg = response.msg
//                }
//
//                is ApiResponses.Exception -> {
//                    handleException(response.e, _uiState)
//                }
//            }
//        }
//    }

    sealed class OrderDetailNavigationEvent(){
        object NavigateBack: OrderDetailNavigationEvent()
    }
}