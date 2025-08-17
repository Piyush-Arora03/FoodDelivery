package com.example.fooddelivery.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.handleException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
    private val _uiState=MutableStateFlow<UiState>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<NavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    fun getRestaurant(){
        viewModelScope.launch{
            _uiState.value=UiState.Loading
            val res=SafeApiCalls {
                foodApi.getRestaurantProfile()
            }.let{
                when (it) {
                    is ApiResponses.Error -> {
                        _uiState.value=UiState.Error(it.msg)
                    }
                    is ApiResponses.Exception -> {
                        _uiState.value=UiState.Error(it.exception.toString())
                    }
                    is ApiResponses.Success -> {
                        _uiState.value=UiState.Success(it.data)
                    }
                }
            }
        }
    }

    fun resetUi(){
        getRestaurant()
    }

    sealed class UiState(){
        object Loading : UiState()
        data class Success(val restaurant: Restaurant): UiState()
        data class Error(val msg:String): UiState()
    }

    fun navigateToOrderDetail(orderId: String){
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToOrderDetail(orderId))
        }
    }

    sealed class NavigationEvent(){
        object NavigateBack: NavigationEvent()
        data class NavigateToOrderDetail(val orderId:String): NavigationEvent()
    }
}