package com.example.fooddelivery.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersListViewModel @Inject constructor(val foodApi: FoodApi):ViewModel() {
    val _uiState= MutableStateFlow<UiState>(UiState.Nothing)
    val uiState=_uiState.asStateFlow()

    val _navigationEvent= MutableSharedFlow<OrderListNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    var errMsg=""

    init {
        getOrderList()
    }

    fun getOrderList() {
        viewModelScope.launch {
            _uiState.value=UiState.Loading
            viewModelScope.launch {
                val res= SafeApiCalls { foodApi.getOrders() }.let {
                    when(it){
                        is ApiResponses.Error -> {
                            _uiState.value=UiState.Error(it.msg)
                            errMsg="Error"
                            _navigationEvent.emit(OrderListNavigationEvent.ShowErrorDialog)
                        }
                        is ApiResponses.Exception -> {
                            _uiState.value=UiState.Error("Something went wrong")
                            errMsg="Error"
                            _navigationEvent.emit(OrderListNavigationEvent.ShowErrorDialog)
                        }
                        is ApiResponses.Success -> {
                            _uiState.value=UiState.OrderList(it.data.orders)
                        }
                    }
                }
            }
        }
    }

    fun navigateBack(){
        viewModelScope.launch {
            _navigationEvent.emit(OrderListNavigationEvent.NavigateBack)
        }
    }

    fun resetUi(){
        _uiState.value=UiState.Nothing
    }

    fun navigateToOrderDetailScreen(order: Order){
        viewModelScope.launch {
            _navigationEvent.emit(OrderListNavigationEvent.NavigateToOrderDetails(order))
        }
    }
    sealed class OrderListNavigationEvent{
        object NavigateBack:OrderListNavigationEvent()
        data class NavigateToOrderDetails(val order:Order):OrderListNavigationEvent()
        object ShowErrorDialog:OrderListNavigationEvent()
    }
    sealed class UiState{
        object Loading:UiState()
        data class Error(val errMsg:String):UiState()
        data class OrderList(val orderList:List<Order>):UiState()
        object Nothing:UiState()
    }
}