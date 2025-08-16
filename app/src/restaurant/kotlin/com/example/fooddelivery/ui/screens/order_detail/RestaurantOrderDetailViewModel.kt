package com.example.fooddelivery.ui.screens.order_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.ui.screens.order_list.OrderListViewModel.OrderDetailState
import com.example.fooddelivery.utils.OrderStatusUtils
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
import com.example.fooddelivery.utils.toError
import com.example.fooddelivery.utils.toLoading
import com.example.fooddelivery.utils.toResetUi
import com.example.fooddelivery.utils.toSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantOrderDetailViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
    data class OrderDetailState(
        val selectedOrder: Order? = null,
        val updateStatusRes: GenericMsgResponse?=null
    )
    private val _uiState= MutableStateFlow<UiState<OrderDetailState>>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<NavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    val types= OrderStatusUtils.OrderStatus.entries.map { it.name }

    fun getOrderDetails(orderId:String){
        _uiState.toLoading()
        viewModelScope.launch {
            SafeApiCalls {
                foodApi.getOrdersDetails(orderId)
            }.let {
                when(it) {
                    is ApiResponses.Error<*> -> {
                        _uiState.toError(it.msg)
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception,_uiState)
                    }
                    is ApiResponses.Success<*> -> {
                        val currentState = (_uiState.value as? UiState.Success)?.data ?: OrderDetailState()
                        _uiState.toSuccess(currentState.copy(selectedOrder = it.data as Order))
                    }
                }
            }
        }
    }

    fun updateOrderStatus(orderId:String,status:String){
        _uiState.toLoading()
        viewModelScope.launch {
            SafeApiCalls {
                foodApi.updateOrderStatus(orderId,mapOf("status" to status))
            }.let {
                when(it) {
                    is ApiResponses.Error<*> -> {
                        _uiState.toError(it.msg)
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception,_uiState)
                    }
                    is ApiResponses.Success<*> -> {
                        _uiState.toSuccess(OrderDetailState().copy(updateStatusRes=it.data as GenericMsgResponse))
                        _navigationEvent.emit(NavigationEvent.ShowPopUp("Order Status Updated Successfully"))
                    }
                }
            }
        }
    }

    fun navigateBack(){
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }

    fun resetUi(orderId: String){
        getOrderDetails(orderId)
    }

    sealed class NavigationEvent(){
        object NavigateBack: NavigationEvent()
        data class ShowPopUp(val msg:String): NavigationEvent()
    }

}