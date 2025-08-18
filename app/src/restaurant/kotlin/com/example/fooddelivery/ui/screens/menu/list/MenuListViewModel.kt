package com.example.fooddelivery.ui.screens.menu.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.FoodItemListResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.ui.screens.home.HomeViewModel
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
import okhttp3.internal.concurrent.formatDuration
import javax.inject.Inject


@HiltViewModel
class MenuListViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel(){
    private val _uiState= MutableStateFlow<UiState<FoodItemListResponse>>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<NavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    fun getMenuItems(restaurantId:String){
        Log.d("resetUi","in getMenuItem...")
        _uiState.toLoading()
        viewModelScope.launch {
            SafeApiCalls {
                foodApi.getRestaurantMenu()
            }.let {
                when(it){
                    is ApiResponses.Error<*> -> {
                        Log.d("resetUi","in getMenuItem error...")
                        _uiState.toError(it.msg)
                    }
                    is ApiResponses.Exception<*> -> {
                        Log.d("resetUi","in getMenuItem exc...")
                        handleException(it.exception,_uiState)
                    }
                    is ApiResponses.Success<*> -> {
                        _uiState.toSuccess(it.data as FoodItemListResponse)
                    }
                }
            }
        }
    }

    fun resetUi(restaurantId:String){
        Log.d("resetUi","in reset UI...")
        getMenuItems(restaurantId)
    }

    fun navigateBack(){
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }

    fun emptyState(){
        _uiState.toEmpty()
    }

    sealed class NavigationEvent(){
        object NavigateBack: NavigationEvent()
    }
}