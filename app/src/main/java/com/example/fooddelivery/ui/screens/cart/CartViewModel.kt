package com.example.fooddelivery.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.CartItem
import com.example.fooddelivery.data.modle.CartResponse
import com.example.fooddelivery.data.modle.UpdateCartItemRequest
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
class CartViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel(){
    private val _uiState= MutableStateFlow<CartUiState>(CartUiState.Nothing)
    val uiState = _uiState.asStateFlow()
    var cartResponse:CartResponse?=null
    private val _navigationEvent = MutableSharedFlow<CartEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    var errMsg=""
    var errDes=""
    private val _cartItemCount= MutableStateFlow(0)
    val cartItemCount=_cartItemCount.asStateFlow()
    init {
        getCart()
    }
    fun getCart(){
        _uiState.value=CartUiState.Loading
        viewModelScope.launch {
            val response= SafeApiCalls {
                foodApi.getCart()
            }
            when(response){
                is ApiResponses.Success->{
                    cartResponse=response.data
                    _cartItemCount.value=response.data.items.size
                    _uiState.value=CartUiState.Success(response.data)
                }
                is ApiResponses.Error->{
                    _uiState.value=CartUiState.Error(response.msg)
                }
                else->{
                    _uiState.value=CartUiState.Error("Something went wrong")
                }
            }
        }
    }

    fun incrementCounter(cartItem: CartItem){
        if(cartItem.quantity==5) return
        updateQuantity(cartItem.id,cartItem.quantity+1)
    }
    fun decrementCounter(cartItem: CartItem){
        if(cartItem.quantity==1) return
        updateQuantity(cartItem.id,cartItem.quantity-1)
    }
    fun removeItem(cartItem: CartItem){
        viewModelScope.launch {
            val res= SafeApiCalls { foodApi.removeItem(cartItem.id) }.let {
                when(it){
                    is ApiResponses.Success->{
                        getCart()
                    }
                    else -> {
                        errMsg="Error"
                        errDes="Something went wrong While Remove Item"
                        _navigationEvent.emit(CartEvent.OnRemoveError)
                        cartResponse?.let {
                            _uiState.value=CartUiState.Success(cartResponse!!)
                        }
                    }
                }
            }
        }
    }
    private fun updateQuantity(cartItemId:String,quantity:Int){
        _uiState.value=CartUiState.Loading
        viewModelScope.launch {
            val res= SafeApiCalls { foodApi.updateQuantity(UpdateCartItemRequest(
                cartItemId = cartItemId,
                quantity = quantity
            )) }.let {
                when(it){
                    is ApiResponses.Success->{
                        getCart()
                        _uiState.value=CartUiState.Success(cartResponse!!)
                    }
                    else -> {
                        _navigationEvent.emit(CartEvent.OnQuantityUpdateError)
                        errMsg="Error"
                        errDes="Something went wrong While Updating Quantity"
                        cartResponse?.let {
                            _uiState.value=CartUiState.Success(cartResponse!!)
                        }
                    }
                }
            }
        }
    }
    fun checkout(){

    }
    fun resetUi(){
        _uiState.value=CartUiState.Nothing
    }

    sealed class CartEvent(){
        object ShowErrorDialog : CartEvent()
        object OnCheckOut : CartEvent()
        object OnQuantityUpdateError:CartEvent()
        object OnRemoveError:CartEvent()
    }

    sealed class CartUiState(){
        object Loading : CartUiState()
        data class Error(val error : String) : CartUiState()
        data class Success(val cartResponse: CartResponse) : CartUiState()
        object Nothing : CartUiState()
    }
}