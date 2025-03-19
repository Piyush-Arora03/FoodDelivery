package com.example.fooddelivery

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fooddelivery.ui.theme.FoodDeliveryTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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
                    Greeting("Piyush Arora", modifier = Modifier.padding(innerPadding))
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            showSplashScreen=false
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodDeliveryTheme {
        Greeting("Android")
    }
}