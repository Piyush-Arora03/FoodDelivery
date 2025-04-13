package com.example.fooddelivery.ui.screens.add_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Address
import com.example.fooddelivery.data.modle.ReverseGeocodeRequest
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(val foodApi: FoodApi,private val locationManager: LocationManager):ViewModel() {
    private val _uiState= MutableStateFlow<AddAddressUiState>(AddAddressUiState.Nothing)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<AddAddressEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    fun getLocation()=locationManager.getLocation()

    private val _address=MutableStateFlow<Address?>(null)
    val address=_address.asStateFlow()

    fun reverseGeocode(lat:Double,lng:Double){
        viewModelScope.launch {
            val res=SafeApiCalls { foodApi.reverseGeocode(ReverseGeocodeRequest(
                lat,
                lng
            )) }.let {
                when(it){
                    is ApiResponses.Success->{
                        _address.value=it.data
                        _uiState.value=AddAddressUiState.Success
                    }
                    is ApiResponses.Error->{
                        _uiState.value=AddAddressUiState.Error(it.msg)
                    }
                    else->{
                        _uiState.value=AddAddressUiState.Error("Failed to get address")
                    }
                }
            }
        }
    }
    fun addAddress(address: Address){

    }

    fun onAddAddressClicked(address: Address){
        viewModelScope.launch {
            _uiState.value=AddAddressUiState.AddressStoring
            val res= SafeApiCalls { foodApi.addAddress(address) }.let {
                when(it){
                    is ApiResponses.Success->{
                        _uiState.value=AddAddressUiState.AddressStoredSuccessfully
                        _navigationEvent.emit(AddAddressEvent.NavigateToAddressList)
                    }
                    is ApiResponses.Error->{
                        _uiState.value=AddAddressUiState.Error(it.msg)
                    }
                    else->{
                        _uiState.value=AddAddressUiState.Error("Something went wrong")
                    }
                }
            }
        }
    }

    sealed class AddAddressEvent{
        object NavigateToAddressList: AddAddressEvent()
        object ShowFinalDialog: AddAddressEvent()
    }
    sealed class AddAddressUiState{
        object Loading: AddAddressUiState()
        object Success: AddAddressUiState()
        data class Error(val errMsg:String): AddAddressUiState()
        object Nothing: AddAddressUiState()
        object AddressStoredSuccessfully: AddAddressUiState()
        object AddressStoring: AddAddressUiState()
    }
}