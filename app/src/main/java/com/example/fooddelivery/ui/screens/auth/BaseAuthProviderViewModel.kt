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
import com.example.fooddelivery.ui.screens.auth.login.SignInViewModel.SignInEvent
import com.example.fooddelivery.ui.screens.auth.login.SignInViewModel.SignInNavigationEvent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseAuthProviderViewModel(open val foodApi: FoodApi) : ViewModel() {
    val TAG="BaseAuthProviderViewModel"
    lateinit var callbackManager: CallbackManager
    var googleAuthUiProvider= GoogleUiProvider()
    abstract fun loading()
    abstract fun socialAuthSuccess(token: String)
    abstract fun facebookError(error: String)
    abstract fun googleError(error: String)

    fun onFacebookSignInClick(context: ComponentActivity) {
        onInitiateFacebookSignIn(context)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun onGoogleSignInClick(context: Context) {
        onInitiateGoogleSignIn(context)
    }

    private fun onInitiateFacebookSignIn(context: ComponentActivity) {
        loading()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(response: LoginResult) {
                    viewModelScope.launch {
                        if(response!=null){
                            val data= OAuthRequest(
                                response.accessToken.token.toString(),
                                provider = "facebook"
                            )
                            val res=foodApi.oAuthRequest(data)
                            if(res.token.isNotEmpty()){
                                Log.d(TAG,response.accessToken.token.toString())
                                socialAuthSuccess(res.token)
                            }else{
                                Log.d(TAG,"error fetching token from backend for facebook")
                                facebookError("facebook error")
                            }
                        }
                    }
                    // App code
                }
                override fun onCancel() {
                    facebookError("facebook cancel")
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    facebookError("facebook error $exception")
                    // App code
                }
            })
        LoginManager.getInstance().logInWithReadPermissions(context,
            callbackManager,
            listOf("email", "public_profile")
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun onInitiateGoogleSignIn(context: Context) {
        viewModelScope.launch {
            loading()
            try{
                val response=googleAuthUiProvider.signInWithGoogle(
                    context,
                    CredentialManager.create(context)
                )
                if(response!=null){
                    val data=OAuthRequest(
                        response.token.toString(),
                        provider = "google"
                    )
                    val res=foodApi.oAuthRequest(data)
                    if(res.token.isNotEmpty()){
                        Log.d(TAG,res.token)
                        socialAuthSuccess(res.token)
                        Log.d(TAG,"Success Google Sign In")
                    }else{
                        Log.d(TAG,"error fetching token from backend for google")
                        googleError("google error")
                    }
                }
            }catch (e:Exception){
                googleError("google error $e")
                e.printStackTrace()
            }
        }
    }
}