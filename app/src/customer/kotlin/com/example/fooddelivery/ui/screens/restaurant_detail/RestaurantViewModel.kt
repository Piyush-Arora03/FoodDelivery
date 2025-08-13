package com.example.fooddelivery.ui.screens.restaurant_detail

import androidx.lifecycle.ViewModel
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.data.modle.FoodItemResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.google.android.gms.common.api.Api
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    var errMsg=""
    var errDescription=""
    private val _uiState=MutableStateFlow<RestaurantUiState>(RestaurantUiState.Nothing)
    val uiState : StateFlow<RestaurantUiState> get() = _uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent : SharedFlow<RestaurantNavigationEvent> get() = _navigationEvent.asSharedFlow()

    suspend fun getFoodItem(id:String){
        SafeApiCalls { foodApi.getFootItem(id) }.let {
            when(it){
                is ApiResponses.Success->{
                    _uiState.value=RestaurantUiState.Success(it.data.foodItems)
                }
                is ApiResponses.Error->{
                    _uiState.value = RestaurantUiState.Error(it.msg)
                    _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                }
                is ApiResponses.Exception->{
                    handleException(it.exception)
                    _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                }
            }
        }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is HttpException -> {
                _uiState.value = RestaurantUiState.Error("HTTP Error: ${exception.code()}")
                errMsg = "Error"
                errDescription = "HTTP Error: ${exception.code()}"
            }

            is IOException -> {
                _uiState.value =
                    RestaurantUiState.Error("Network Error: Please check your internet connection")
                errMsg = "Error"
                errDescription = "Network Error: Please check your internet connection"
            }

            else -> {
                _uiState.value = RestaurantUiState.Error("Something went wrong")
                errMsg = "Error"
                errDescription = "Something went wrong"
            }
        }
    }

    fun resetUi(){
        _uiState.value=RestaurantUiState.Nothing
    }
    sealed class RestaurantNavigationEvent{
        object GoBack:RestaurantNavigationEvent()
        data class ToProductDetail(val productId:String):RestaurantNavigationEvent()
        object ShowErrorDialog:RestaurantNavigationEvent()
    }

    sealed class RestaurantUiState{
        object Loading:RestaurantUiState()
        data class Error(val message: String):RestaurantUiState()
        data class Success(val data: List<FoodItem>):RestaurantUiState()
        object Nothing:RestaurantUiState()
    }
}