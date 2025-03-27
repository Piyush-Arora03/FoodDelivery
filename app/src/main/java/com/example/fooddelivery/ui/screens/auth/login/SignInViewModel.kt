package com.example.fooddelivery.ui.screens.auth.login

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.auth.GoogleUiProvider
import com.example.fooddelivery.data.modle.OAuthRequest
import com.example.fooddelivery.data.modle.SignInRequest
import com.example.fooddelivery.ui.screens.auth.BaseAuthProviderViewModel
import com.example.fooddelivery.ui.screens.auth.signup.SignUpViewModel.SignUpNavigationEvent
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(override val foodApi: FoodApi): BaseAuthProviderViewModel(foodApi = foodApi) {

    private val _uiState= MutableStateFlow<SignInEvent>(SignInEvent.EventNothing)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private val _email= MutableStateFlow<String>("")
    val email=_email.asStateFlow()

    private val _password= MutableStateFlow<String>("")
    val password=_password.asStateFlow()

    fun onEmailChange(email: String){
        _email.value=email
    }

    fun onPasswordChange(password: String){
        _password.value=password
    }
    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value=SignInEvent.EventLoading
            try {
                val response=foodApi.signInRequest(
                    SignInRequest(
                    email.value,password.value
                )
                )
                if(response.body()!!.token.isNotEmpty()){
                    _uiState.value=SignInEvent.EventSuccess
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
                else{
                    _uiState.value=SignInEvent.EventError
                }
            }catch (e:Exception){
                e.printStackTrace()
                _uiState.value=SignInEvent.EventError
            }
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigateToSignup)
        }
    }

    sealed class SignInNavigationEvent{
        object NavigateToSignup: SignInNavigationEvent()
        object NavigateToHome: SignInNavigationEvent()
    }

    sealed class SignInEvent{
        object EventLoading: SignInEvent()
        object EventSuccess: SignInEvent()
        object EventError: SignInEvent()
        object EventNothing: SignInEvent()
    }
    override fun loading() {
        viewModelScope.launch {
            _uiState.value=SignInEvent.EventLoading
        }
    }

    override fun socialAuthSuccess(token: String) {
        viewModelScope.launch {
            _uiState.value=SignInEvent.EventSuccess
            _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
        }
    }

    override fun googleError(msg : String) {
        viewModelScope.launch {
            _uiState.value=SignInEvent.EventError
        }
    }

    override fun facebookError(msg: String) {
        viewModelScope.launch {
            _uiState.value=SignInEvent.EventError
        }
    }
}