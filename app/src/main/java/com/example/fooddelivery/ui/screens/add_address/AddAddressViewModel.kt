package com.example.fooddelivery.ui.screens.add_address

import androidx.lifecycle.ViewModel
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Address
import com.example.fooddelivery.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(val foodApi: FoodApi,private val locationManager: LocationManager):ViewModel() {
    private val _uiState= MutableStateFlow<AddAddressUiState>(AddAddressUiState.Nothing)
    val uiState=_uiState.asStateFlow()
    private val _navigationEvent= MutableSharedFlow<AddAddressEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()
    fun getLocation()=locationManager.getLocation()
    fun reverseGeocode(lat:Double,lng:Double){

    }
    fun addAddress(address: Address){}

    sealed class AddAddressEvent{
        object NavigateToAddressList: AddAddressEvent()
    }
    sealed class AddAddressUiState{
        object Loading: AddAddressUiState()
        object Success: AddAddressUiState()
        data class Error(val errMsg:String): AddAddressUiState()
        object Nothing: AddAddressUiState()
    }
}