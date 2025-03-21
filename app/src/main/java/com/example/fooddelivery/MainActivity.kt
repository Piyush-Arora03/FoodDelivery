package com.example.fooddelivery

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.navigation.AuthScreen
import com.example.fooddelivery.navigation.HomeScreen
import com.example.fooddelivery.navigation.LogInScreen
import com.example.fooddelivery.navigation.SignUpScreen
import com.example.fooddelivery.ui.screens.auth.AuthScreen
import com.example.fooddelivery.ui.screens.auth.signup.SignUpScreen
import com.example.fooddelivery.ui.theme.FoodDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var foodApi: FoodApi
    val TAG="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var showSplashScreen=true
        installSplashScreen().apply {
            setKeepOnScreenCondition(){
                showSplashScreen
            }
            setOnExitAnimationListener{ screen->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_X,0.5f,0.0f
                )
                val zoomY=ObjectAnimator.ofFloat(
                    screen.iconView,View.SCALE_Y,0.5f,0.0f
                )
                zoomX.duration=1000
                zoomY.duration=1000
                zoomX.interpolator=OvershootInterpolator()
                zoomY.interpolator=OvershootInterpolator()
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        setContent {
            FoodDeliveryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController= rememberNavController()
                    val navHost= NavHost(navController = navController, startDestination = AuthScreen, modifier = Modifier.padding(innerPadding)){
                        composable<AuthScreen> {
                            AuthScreen(navController)
                        }
                        composable<SignUpScreen> {
                            SignUpScreen(navController=navController)
                        }
                        composable<HomeScreen> {
                            Box(modifier = Modifier.fillMaxSize().
                            background(color = Color.White))
                        }
                        composable<LogInScreen> {
                            Box(modifier = Modifier.fillMaxSize().
                            background(color = Color.Red))
                        }
                    }
                }
            }
        }
        if(::foodApi.isInitialized){
            Log.d(TAG,"FoodApi is initialized")
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            showSplashScreen=false
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodDeliveryTheme {
    }
}