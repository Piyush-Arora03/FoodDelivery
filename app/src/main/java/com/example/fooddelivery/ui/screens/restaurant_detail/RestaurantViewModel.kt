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
                    val error=(it as ApiResponses.Error).code
                    when(error){
                        404->{
                            errMsg="Not Found"
                            errDescription="The requested resource was not found"
                        }
                        401->{
                            errMsg="Unauthorized"
                            errDescription="The request requires user authentication"
                        }
                        500->{
                            errMsg="Internal Server Error"
                            errDescription="The server encountered an unexpected condition that prevented it from fulfilling the request"
                        }
                        else->{
                            errMsg="Something went wrong"
                            errDescription="The request could not be completed due to a network error"
                        }
                    }
                    _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                    _uiState.value=RestaurantUiState.Error
                }
                is ApiResponses.Exception->{
                    errMsg="Something went wrong"
                    errDescription=(it as ApiResponses.Exception).exception
                    _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                    _uiState.value=RestaurantUiState.Error
                }
            }
        }
    }
    sealed class RestaurantNavigationEvent{
        object GoBack:RestaurantNavigationEvent()
        data class ToProductDetail(val productId:String):RestaurantNavigationEvent()
        object ShowErrorDialog:RestaurantNavigationEvent()
    }

    sealed class RestaurantUiState{
        object Loading:RestaurantUiState()
        object Error:RestaurantUiState()
        data class Success(val data: List<FoodItem>):RestaurantUiState()
        object Nothing:RestaurantUiState()
    }
}