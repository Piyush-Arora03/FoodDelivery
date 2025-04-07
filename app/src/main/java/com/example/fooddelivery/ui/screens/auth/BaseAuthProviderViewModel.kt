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
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.auth.GoogleUiProvider
import com.example.fooddelivery.data.modle.OAuthRequest
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch

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
                        fetchApiData(response.accessToken.toString(), "google", ::googleError)
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
            try {
                val response = googleAuthUiProvider.signInWithGoogle(
                    context,
                    CredentialManager.create(context)
                )
                if (response != null) {
                    fetchApiData(response.token.toString(), "google", ::googleError)
                }
            } catch (e: Exception) {
                googleError("google error $e")
                e.printStackTrace()
            }
        }
    }
    private fun fetchApiData(token: String,provider:String,onError:(String)->Unit){
        viewModelScope.launch {
            val data = OAuthRequest(
                token.toString(),
                provider = provider
            )
            val res = SafeApiCalls { foodApi.oAuthRequest(data) }
            when (res) {
                is ApiResponses.Success -> {
                    socialAuthSuccess(res.data.token)

                }
                else -> {
                    Log.d("TAGe","errorOccured")
                    val error = (res as? ApiResponses.Error)?.code
                    if (error != null) {
                        when (error) {
                            404 -> onError("user not found")
                            401 -> onError("unauthorized")
                            500 -> onError("internal server error")
                            else -> onError("something went wrong")
                        }
                    } else {
                        onError("error code null")
                    }
                }
            }
        }
    }
}