package com.example.fooddelivery.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.modle.Category
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodApi:FoodApi) :ViewModel(){
    val TAG="HomeViewModel"
    private val _uiState= MutableStateFlow<HomeScreenState>(HomeScreenState.Empty)
    val uiState:StateFlow<HomeScreenState> =_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<HomeScreenNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    init {
        getCategories()
        getPopularRestaurants()
    }
    val categories= mutableListOf<Category>()
    fun getCategories(){
        viewModelScope.launch {
            SafeApiCalls { foodApi.getCategories() }.let {
                when(it){
                    is ApiResponses.Success->{
                        _uiState.value=HomeScreenState.Success
                        categories.addAll(it.data.data)
                        Log.d(TAG,categories.toString())
                    }
                    is ApiResponses.Error->{
                        _uiState.value=HomeScreenState.Error
                    }
                    else->{
                        _uiState.value=HomeScreenState.Empty
                    }
                }
            }
        }
    }


    fun getPopularRestaurants(){

    }

    sealed class HomeScreenState{
        object Loading:HomeScreenState()
        object Success:HomeScreenState()
        object Error:HomeScreenState()
        object Empty:HomeScreenState()
    }

    sealed class HomeScreenNavigationEvent{
        object NavigateToDetail:HomeScreenNavigationEvent()
    }
}