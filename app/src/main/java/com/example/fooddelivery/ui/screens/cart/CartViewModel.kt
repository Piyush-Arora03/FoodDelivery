package com.example.fooddelivery.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.modle.Address
import com.example.fooddelivery.data.modle.CartItem
import com.example.fooddelivery.data.modle.CartResponse
import com.example.fooddelivery.data.modle.ConfirmPaymentRequest
import com.example.fooddelivery.data.modle.PaymentIntentRequest
import com.example.fooddelivery.data.modle.PaymentIntentResponse
import com.example.fooddelivery.data.modle.UpdateCartItemRequest
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(val foodApi: FoodApi,val authSession: FoodHubAuthSession) : ViewModel(){
    private val _uiState= MutableStateFlow<CartUiState>(CartUiState.Nothing)
    val uiState = _uiState.asStateFlow()
    var cartResponse:CartResponse?=null
    private val _navigationEvent = MutableSharedFlow<CartEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    var errMsg=""
    var errDes=""
    private val _cartItemCount= MutableStateFlow(0)
    val cartItemCount=_cartItemCount.asStateFlow()
    private val _address= MutableStateFlow<Address?>(null)
    val address=_address.asStateFlow()
    private var paymentIntentResponse:PaymentIntentResponse?=null
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
        viewModelScope.launch {
            _uiState.value=CartUiState.Loading
            val res= SafeApiCalls { foodApi.createPaymentIntent(
                PaymentIntentRequest(address.value!!.id!!)
            ) }.let {
                when(it){
                    is ApiResponses.Error -> {
                        _uiState.value=CartUiState.Error(it.msg)
                        _navigationEvent.emit(CartEvent.ShowErrorDialog)
                        errMsg="Error"
                        errDes=it.msg
                    }
                    is ApiResponses.Exception -> {
                        _uiState.value=CartUiState.Error("Something went wrong")
                        _navigationEvent.emit(CartEvent.ShowErrorDialog)
                        errMsg="Error"
                        errDes="Something went wrong"
                    }
                    is ApiResponses.Success -> {
                        paymentIntentResponse=it.data
                        getCart()
                        _navigationEvent.emit(CartEvent.OnPaymentInitiated(it.data))
                        _uiState.value=CartUiState.Success(cartResponse!!)
                    }
                }
            }
        }
    }
    fun resetUi(){
        _uiState.value=CartUiState.Nothing
    }
    fun onAddressClicked(){
        viewModelScope.launch {
            _navigationEvent.emit(CartEvent.OnAddressClicked)
        }
    }

    fun onAddressSelected(value: Address) {
        _address.value=value
    }
    fun onPaymentFailed(){
        errMsg="Payment Failed"
        errDes="An error occurred while processing the payment"
        viewModelScope.launch {
            _navigationEvent.emit(CartEvent.ShowErrorDialog)
        }
    }
    fun onPaymentSuccess() {
        viewModelScope.launch {
            val res= SafeApiCalls { foodApi.verifyPurchase(
                ConfirmPaymentRequest(
                    paymentIntentResponse?.paymentIntentId?:"",
                    address.value!!.id!!
                ),paymentIntentResponse?.paymentIntentId?:""
                ) }.let {
                when(it){
                    is ApiResponses.Success->{
                        _navigationEvent.emit(CartEvent.OnPaymentSuccess(it.data.orderId))
                        getCart()
                        _uiState.value=CartUiState.Success(cartResponse!!)
                    }
                    is ApiResponses.Error -> {
                        _uiState.value=CartUiState.Error(it.msg)
                        _navigationEvent.emit(CartEvent.ShowErrorDialog)
                        errMsg="Error"
                        errDes=it.msg
                    }
                    is ApiResponses.Exception -> {
                        _uiState.value=CartUiState.Error("Something went wrong")
                        _navigationEvent.emit(CartEvent.ShowErrorDialog)
                        errMsg="Error"
                        errDes="Something went wrong"
                    }
                }
            }
        }
    }

    sealed class CartEvent(){
        object ShowErrorDialog : CartEvent()
        object OnCheckOut : CartEvent()
        object OnQuantityUpdateError:CartEvent()
        object OnRemoveError:CartEvent()
        object OnAddressClicked: CartEvent()
        data class OnPaymentInitiated(val data: PaymentIntentResponse) : CartEvent()
        data class OnPaymentSuccess(val orderId:String?):CartEvent()
    }

    sealed class CartUiState(){
        object Loading : CartUiState()
        data class Error(val error : String) : CartUiState()
        data class Success(val cartResponse: CartResponse) : CartUiState()
        object Nothing : CartUiState()
    }
}