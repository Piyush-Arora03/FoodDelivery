package com.example.fooddelivery.ui.screens.auth.signup

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.modle.SignUpRequest
import com.example.fooddelivery.ui.screens.auth.BaseAuthProviderViewModel
import com.example.fooddelivery.ui.screens.auth.login.SignInViewModel.SignInEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(override val foodApi: FoodApi,private val session: FoodHubAuthSession): BaseAuthProviderViewModel(foodApi){
    var msg=""
    var dis=""
    private val _uiState= MutableStateFlow<SignUpEvent>(SignUpEvent.EventNothing)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private val _name= MutableStateFlow<String>("")
    val name=_name.asStateFlow()

    private val _email= MutableStateFlow<String>("")
    val email=_email.asStateFlow()

    private val _password= MutableStateFlow<String>("")
    val password=_password.asStateFlow()

    fun onNameChange(name: String){
        _name.value=name
    }

    fun onEmailChange(email: String){
        _email.value=email
    }

    fun onPasswordChange(password: String){
        _password.value=password
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value=SignUpEvent.EventLoading
            try {
                val response=foodApi.signUpRequest(SignUpRequest(
                    name.value,email.value,password.value
                ))
                if(response.body()!!.token.isNotEmpty()){
                    _uiState.value=SignUpEvent.EventSuccess
                    session.saveToken(response.body()!!.token)
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
                }
                else{
                    _uiState.value=SignUpEvent.EventError
                    msg="Error"
                    dis="hsakghkasjhvkaskjbkjas asdvvs"
                    _uiState.emit(SignUpEvent.ShowErrorDialog)
                }
            }catch (e:Exception){
                e.printStackTrace()
                _uiState.value=SignUpEvent.EventError
                msg="Error"
                dis=e.toString()
                _uiState.emit(SignUpEvent.ShowErrorDialog)
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
        }
    }

    sealed class SignUpNavigationEvent{
        object NavigateToLogin: SignUpNavigationEvent()
        object NavigateToHome: SignUpNavigationEvent()
    }

    sealed class SignUpEvent{
        object EventLoading: SignUpEvent()
        object EventSuccess: SignUpEvent()
        object EventError: SignUpEvent()
        object EventNothing: SignUpEvent()
        object ShowErrorDialog: SignUpEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            Log.d(TAG,"loading")
            _uiState.value= SignUpEvent.EventLoading
        }
    }

    override fun socialAuthSuccess(msg: String) {
        viewModelScope.launch {
            Log.d(TAG,"socialAuthSuccess")
            _uiState.value= SignUpEvent.EventSuccess
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
        }
    }

    override fun googleError(msg: String) {
        viewModelScope.launch {
            Log.d(TAG,"googleError")
            _uiState.value= SignUpEvent.EventError
        }
    }

    override fun facebookError(msg: String) {
        viewModelScope.launch {
            Log.d(TAG,"facebookError")
            _uiState.value= SignUpEvent.EventError
        }
    }
}