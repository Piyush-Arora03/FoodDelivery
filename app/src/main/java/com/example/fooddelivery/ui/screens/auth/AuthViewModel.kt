package com.example.fooddelivery.ui.screens.auth

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
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(override val foodApi: FoodApi): BaseAuthProviderViewModel(foodApi = foodApi) {

    private val _uiState= MutableStateFlow<AuthEvent>(AuthEvent.EventNothing)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(AuthNavigationEvent.NavigateToSignup)
        }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _navigationEvent.emit(AuthNavigationEvent.NavigateToSignIn)
        }
    }

    sealed class AuthNavigationEvent{
        object NavigateToSignup: AuthNavigationEvent()
        object NavigateToSignIn: AuthNavigationEvent()
        object NavigateToHome: AuthNavigationEvent()
    }

    sealed class AuthEvent{
        object EventLoading: AuthEvent()
        object EventSuccess: AuthEvent()
        object EventError: AuthEvent()
        object EventNothing: AuthEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value=AuthEvent.EventLoading
        }
    }

    override fun socialAuthSuccess(msg: String) {
        viewModelScope.launch {
            _uiState.value=AuthEvent.EventSuccess
            _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
        }
    }

    override fun googleError(msg: String) {
        viewModelScope.launch {
            _uiState.value=AuthEvent.EventError
        }
    }

    override fun facebookError(msg: String) {
        viewModelScope.launch {
            _uiState.value=AuthEvent.EventError
        }
    }
}