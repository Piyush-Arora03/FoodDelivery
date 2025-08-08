package com.example.fooddelivery.ui.screens.order_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
@HiltViewModel
class OrderDetailViewModel @Inject constructor (val foodApi: FoodApi): ViewModel() {

    val _uiState= MutableStateFlow<UiState>(UiState.Nothing)
    val uiState=_uiState.asStateFlow()

    val _navigationEvent= MutableSharedFlow<OrderDetailNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    var errMsg=""

    fun getOrderDetail(orderId:String){
        _uiState.value=UiState.Loading
        viewModelScope.launch {
            SafeApiCalls { foodApi.getOrdersDetails(orderId)}.let {
                when(it){
                    is ApiResponses.Success -> _uiState.value=UiState.OrderDetail(it.data)
                    is ApiResponses.Error -> {
                        errMsg=it.msg
                        _uiState.value=UiState.Error(errMsg)
                    }
                    is ApiResponses.Exception -> {
                        handleException(it.exception)
                    }
                }
            }
        }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is HttpException -> {
                _uiState.value = UiState.Error("HTTP Error: ${exception.code()}")
                errMsg = "Error"
            }

            is IOException -> {
                _uiState.value =
                    UiState.Error("Network Error: Please check your internet connection")
                errMsg = "Error"
            }

            else -> {
                _uiState.value = UiState.Error("Something went wrong")
                errMsg = "Error"
            }
        }
    }

    fun resetUi(orderId:String){
        viewModelScope.launch {
            getOrderDetail(orderId)
        }
    }

    sealed class OrderDetailNavigationEvent{
        object NavigateBack:OrderDetailNavigationEvent()
        data class NavigateToOrderDetails(val order:Order):OrderDetailNavigationEvent()
        object ShowErrorDialog:OrderDetailNavigationEvent()
    }
    sealed class UiState{
        object Loading:UiState()
        data class Error(val errMsg:String):UiState()
        data class OrderDetail(val orderDetail:Order):UiState()
        object Nothing:UiState()
    }
}