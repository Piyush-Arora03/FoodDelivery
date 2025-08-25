package com.example.fooddelivery.ui.screens.order_detail.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.RiderActiveDeliveries
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
import com.example.fooddelivery.utils.toEmpty
import com.example.fooddelivery.utils.toError
import com.example.fooddelivery.utils.toLoading
import com.example.fooddelivery.utils.toSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel(){
    private val _uiState= MutableStateFlow<UiState<RiderActiveDeliveries>>(UiState.Loading)
    val uiState=_uiState.asStateFlow()

    init {
        getActiveDeliveries()
    }

    fun getActiveDeliveries(){
        _uiState.toLoading()
        viewModelScope.launch {
            SafeApiCalls { foodApi.getActiveDeliveries() }.let {
                when(it){
                    is ApiResponses.Error<*> ->{
                        _uiState.toError(it.msg)
                    }
                    is ApiResponses.Exception<*> ->{
                        handleException(it.exception,_uiState)
                    }
                    is ApiResponses.Success<*> -> {
                        _uiState.toSuccess(it.data as RiderActiveDeliveries)
                    }
                }
            }
        }
    }

    fun resetUi(){
        getActiveDeliveries()
    }

    fun emptyState(){
        _uiState.toEmpty()
    }
}