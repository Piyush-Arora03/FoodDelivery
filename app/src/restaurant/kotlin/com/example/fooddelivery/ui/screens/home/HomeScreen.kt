package com.example.fooddelivery.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController,homeViewModel: HomeViewModel= hiltViewModel()) {
    val uiState=homeViewModel.uiState.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) {
        homeViewModel.navigationEvent.collectLatest {
            when(it){
                is HomeViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is HomeViewModel.NavigationEvent.NavigateToOrderDetail -> {
                    navController.navigate(OrderDetailScreen(it.orderId))
                }
            }
        }
    }
    val TAG="RestaurantHome"
    LaunchedEffect(Unit) {
        homeViewModel.getRestaurant()
    }

    Column(modifier = Modifier
        .fillMaxSize()) {
        HeaderView(
            {
                navController.popBackStack()
            },
            name = "Home",
        )
        when(uiState){
            is HomeViewModel.UiState.Loading -> {
                Loading()
            }
            is HomeViewModel.UiState.Error -> {
                Log.d(TAG,uiState.msg)
            }
            is HomeViewModel.UiState.Success -> {
                Log.d(TAG,uiState.restaurant.toString())
            }
        }
    }


}