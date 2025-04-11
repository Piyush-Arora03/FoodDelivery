package com.example.fooddelivery.ui.screens.food_detail

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.AddToCartRequest
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.ui.screens.restaurant_detail.RestaurantViewModel.RestaurantUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(val foodApi: FoodApi) :ViewModel(){
    private val _uiState= MutableStateFlow<FoodDetailUiState>(FoodDetailUiState.Nothing)
    val uiState = _uiState

    private val _navigationEvent= MutableSharedFlow<FoodDetailEvent>()
    val navigationEvent=_navigationEvent

    val _quantity=MutableStateFlow<Int>(1)
    val quantity= _quantity

    fun incrementQuantity(){
        if(_quantity.value<10) {
            _quantity.value = _quantity.value + 1
        }
    }
    fun decrementQuantity(){
        if(_quantity.value>1) {
            _quantity.value = _quantity.value - 1
        }
    }

    fun addToCart(restaurantId:String,foodItemId:String){
        viewModelScope.launch {
            _uiState.value=FoodDetailUiState.Loading
            val response= SafeApiCalls {
                foodApi.addToCart(
                    AddToCartRequest(
                        menuItemId = foodItemId,
                        quantity = _quantity.value,
                        restaurantId = restaurantId
                    )
                )
            }.let {
                when(it){
                    is ApiResponses.Success->{
                        _uiState.value=FoodDetailUiState.Success
                        _navigationEvent.emit(FoodDetailEvent.OnAddToCart)
                    }
                    is ApiResponses.Error->{
                        _uiState.value=FoodDetailUiState.Error(it.msg)
                        _navigationEvent.emit(FoodDetailEvent.ShowErrorDialog(it.msg))
                    }
                    else->{
                        _uiState.value=FoodDetailUiState.Error("Something went wrong")
                        _navigationEvent.emit(FoodDetailEvent.ShowErrorDialog("Something went wrong"))
                    }
                }
            }
        }
    }
    fun resetUi(){
        _uiState.value= FoodDetailUiState.Nothing
    }
    sealed class FoodDetailUiState{
        object Loading : FoodDetailUiState()
        object Success : FoodDetailUiState()
        data class Error(val errMsg:String) : FoodDetailUiState()
        object Nothing : FoodDetailUiState()
    }

    sealed class FoodDetailEvent{
        data class ShowErrorDialog(val errMsg:String) : FoodDetailEvent()
        object OnAddToCart : FoodDetailEvent()
    }
}