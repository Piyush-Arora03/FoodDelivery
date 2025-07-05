package com.example.fooddelivery.ui.screens.address_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Address
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel@Inject constructor(val foodApi: FoodApi):ViewModel(){
    private val _uiState= MutableStateFlow<AddressListUiState>(AddressListUiState.Nothing)
    val uiState=_uiState.asStateFlow()
    private val _navigationEvent= MutableSharedFlow<AddressListEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()
    init{
        getAddress()
    }
    fun getAddress(){
        viewModelScope.launch {
            _uiState.value=AddressListUiState.Loading
            val res= SafeApiCalls { foodApi.getAddresses() }.let{
                when(it){
                    is ApiResponses.Success->{
                        _uiState.value=AddressListUiState.Success(it.data.addresses)
                    }
                    is ApiResponses.Error->{
                        _uiState.value=AddressListUiState.Error(it.msg)
                    }
                    else->{
                        _uiState.value=AddressListUiState.Error("Something went wrong")
                    }
                }
            }
        }
    }
    fun onAddressClicked(address: Address) {
        viewModelScope.launch {
            _navigationEvent.emit(AddressListEvent.NavigateBack(address))
        }
    }
    sealed class AddressListEvent{
        object NavigateToEditAddress : AddressListEvent()
        object NavigateToAddAddress : AddressListEvent()
        data class NavigateBack(val address: Address) : AddressListEvent()
    }

    sealed class AddressListUiState{
        object Loading : AddressListUiState()
        data class Success(val data:List<Address>) : AddressListUiState()
        data class Error(val errMsg:String) : AddressListUiState()
        object Nothing : AddressListUiState()
    }
}