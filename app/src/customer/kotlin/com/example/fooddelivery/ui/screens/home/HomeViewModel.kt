package com.example.fooddelivery.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Category
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.data.modle.RestaurantResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodApi:FoodApi) :ViewModel(){
    val TAG="HomeViewModel"
    private val _uiState= MutableStateFlow<HomeScreenState>(HomeScreenState.Empty)
    val uiState:StateFlow<HomeScreenState> =_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<HomeScreenNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()
    var categories= emptyList<Category>()
    var restaurants= emptyList<Restaurant>()
    init {
        viewModelScope.launch {
            categories=getCategories()
            restaurants=getPopularRestaurants()
            if(categories.isNotEmpty() && restaurants.isNotEmpty()){
                _uiState.value=HomeScreenState.Success
            }
            else{
                _uiState.value=HomeScreenState.Empty
            }
        }
    }
    suspend fun getCategories(): List<Category> {
            SafeApiCalls { foodApi.getCategories() }.let {
                when(it){
                    is ApiResponses.Success->{
                        categories=it.data.data
                        Log.d(TAG,categories.toString())
                    }
                    is ApiResponses.Error->{
                        _uiState.value=HomeScreenState.Error(it.msg)
                    }
                    is ApiResponses.Exception -> {
                        handleException(it.exception)
                    }
                }
            }
        return categories
    }


    suspend fun getPopularRestaurants():List<Restaurant>{
        SafeApiCalls {
            foodApi.getRestaurants(40.7128,-74.0060)
        }.let {
            when(it){
                is ApiResponses.Success->{
                    restaurants=it.data.data
                    Log.d(TAG,restaurants.toString())
                }
                is ApiResponses.Error->{
                    _uiState.value=HomeScreenState.Error(it.msg)
                }
                is ApiResponses.Exception -> {
                    handleException(it.exception)
                }
            }
        }
        return restaurants
    }

    fun navigateToDetails(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeScreenNavigationEvent.NavigateToDetail(it.id,it.name,it.imageUrl))
        }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is HttpException -> {
                _uiState.value = HomeScreenState.Error("HTTP Error: ${exception.code()}")
            }

            is IOException -> {
                _uiState.value =
                    HomeScreenState.Error("Network Error: Please check your internet connection")
            }

            else -> {
                _uiState.value = HomeScreenState.Error("Something went wrong")
            }
        }
    }

    sealed class HomeScreenState{
        object Loading:HomeScreenState()
        object Success:HomeScreenState()
        data class Error(val message: String):HomeScreenState()
        object Empty:HomeScreenState()
    }

    sealed class HomeScreenNavigationEvent{
        data class NavigateToDetail(val id:String,val name:String,val imageUrl:String):HomeScreenNavigationEvent()
    }
}