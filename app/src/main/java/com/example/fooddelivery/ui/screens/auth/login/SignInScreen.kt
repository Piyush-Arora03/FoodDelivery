package com.example.fooddelivery.ui.screens.auth.signup

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.navigation.AuthScreen
import com.example.fooddelivery.navigation.HomeScreen
import com.example.fooddelivery.navigation.LogInScreen
import com.example.fooddelivery.navigation.SignUpScreen
import com.example.fooddelivery.ui.FoodHubTextFiled
import com.example.fooddelivery.ui.GroupSocialButtons
import com.example.fooddelivery.ui.screens.auth.login.SignInViewModel
import com.example.fooddelivery.ui.theme.FoodDeliveryTheme
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.poppinsFontFamily
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SignInScreen (viewModel: SignInViewModel= hiltViewModel(),navController: NavController) {
    val email= viewModel.email.collectAsStateWithLifecycle()
    val password= viewModel.password.collectAsStateWithLifecycle()
    val errorMessage= remember {
        mutableStateOf("")
    }
    val loading= remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current
    when(viewModel.uiState.collectAsStateWithLifecycle().value){
        SignInViewModel.SignInEvent.EventSuccess ->{
            loading.value=false
            errorMessage.value=""
        }
        SignInViewModel.SignInEvent.EventLoading -> {
            loading.value=true
            errorMessage.value=""
        }
        SignInViewModel.SignInEvent.EventError -> {
            loading.value=false
            errorMessage.value="Failed to sign in"
        }
        else -> {
            loading.value=false
            errorMessage.value=""
        }
    }
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest{ event ->
            when(event){
                SignInViewModel.SignInNavigationEvent.NavigateToHome->{
                    navController.navigate(HomeScreen){
                        Log.d("TAG","Navigating to home screen")
                        popUpTo(AuthScreen){
                            inclusive=true
                        }
                    }
                }
                SignInViewModel.SignInNavigationEvent.NavigateToSignup->{
                    navController.navigate(SignUpScreen)
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(R.drawable.signup_background),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds)
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text ="Login",
                style = TextStyle(
                    fontSize = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFontFamily,
                    fontStyle = FontStyle.Normal,
                ),
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .padding(start = 20.dp)
            )
            Spacer(modifier =Modifier.padding(bottom = 30.dp))
            FoodHubTextFiled(
                value = email.value,
                onValueChange = {viewModel.onEmailChange(it)},
                label = {
                    Text(text = stringResource(R.string.email), color = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLines = 1
            )
            Spacer(modifier =Modifier.padding(bottom = 30.dp))
            FoodHubTextFiled(
                value = password.value,
                onValueChange = {viewModel.onPasswordChange(it)},
                label = {
                    Text(text = stringResource(R.string.password), color = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Image(painter = painterResource(R.drawable.ic_eye),
                        contentDescription = null,)
                },
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier =Modifier.padding(bottom = 50.dp))
            Button(onClick = {
                viewModel.onSignInClick()
            }, colors = ButtonDefaults.buttonColors(Orange),
                modifier = Modifier
                    .fillMaxWidth(0.6f)) {
                Box(){
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300))+ scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                        }
                    ) { target->
                        if(target){
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(vertical = 2.dp))
                        }
                        else{
                            Text(text = stringResource(R.string.sign_up), modifier = Modifier
                                .padding(vertical = 12.dp)
                                .align(Alignment.Center),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = poppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(bottom = 30.dp))
            Text(text = stringResource(R.string.dont_have_an_account), modifier = Modifier.clickable {
                viewModel.onSignUpClick()
            })
            Spacer(modifier = Modifier.padding(bottom = 50.dp))
            GroupSocialButtons(text= R.string.sign_in_with, viewModel = viewModel)
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun Preview() {
//    SignUpScreen()
//}