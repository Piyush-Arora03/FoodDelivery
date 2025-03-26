package com.example.fooddelivery.ui.screens.auth

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.navigation.HomeScreen
import com.example.fooddelivery.navigation.LogInScreen
import com.example.fooddelivery.navigation.SignUpScreen
import com.example.fooddelivery.ui.GroupSocialButtons
import com.example.fooddelivery.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AuthScreen(navController: NavController,viewModel: AuthViewModel= hiltViewModel()) {
    val imageSize= remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush=Brush.verticalGradient(
        listOf(
            Color.Transparent,Color.Black
        ),
        startY = imageSize.value.height.toFloat()/2,
    )
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest{ event ->
            when(event){
                AuthViewModel.AuthNavigationEvent.NavigateToHome->{
                    navController.navigate(HomeScreen){
                        Log.d("TAG","Navigating to home screen")
                        popUpTo(com.example.fooddelivery.navigation.AuthScreen){
                            inclusive=true
                        }
                    }
                }
                AuthViewModel.AuthNavigationEvent.NavigateToSignup->{
                    navController.navigate(SignUpScreen)
                }
                AuthViewModel.AuthNavigationEvent.NavigateToSignIn->{
                    navController.navigate(LogInScreen)
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
    ){
        Image(painter = painterResource(R.drawable.auth_background), contentDescription = null,
            modifier = Modifier.
                fillMaxSize()
                .matchParentSize()
                .alpha(0.8f)
                .onGloballyPositioned {
                    imageSize.value=it.size
                },
            contentScale = ContentScale.FillBounds
                )
        Box(
            modifier = Modifier.matchParentSize()
                .background(brush),
        ){
            Image(painter = painterResource(R.drawable.text_welcome), contentDescription = null
                , alignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = (imageSize.value.height/10).dp)
            )
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.padding(8.dp)
                        .align(Alignment.TopEnd)
        ) {
                Text(text = stringResource(R.string.skip), color = Orange)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(align = Alignment.Bottom)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        GroupSocialButtons(text = R.string.sign_in_with, color = Color.Gray.copy(alpha = 0.2f), viewModel = viewModel)
        Button(onClick = {
            navController.navigate(LogInScreen)
        },
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth().padding(10.dp)
                .border(1.dp, Color.White, RoundedCornerShape(32.dp))
        ) {
            Text(text = stringResource(R.string.sign_in_with_email), color = Color.White)
        }
        Text(text = stringResource(R.string.dont_have_an_account), color = Color.White,
            modifier = Modifier.clickable {
                navController.navigate(SignUpScreen)
            })
    }

}



