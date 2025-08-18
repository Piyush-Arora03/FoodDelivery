package com.example.fooddelivery.ui.screens.order_success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.fooddelivery.navigation.HomeScreen

@Composable
fun OrderSuccess(orderId:String,navController: NavController) {
    BackHandler {
        navController.popBackStack(route = HomeScreen, inclusive = false)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text="Order Placed Successfully", style = MaterialTheme.typography.titleMedium)
        Text(text = "Order Id: $orderId", style = MaterialTheme.typography.bodyMedium
        , color = Color.Gray)
        Button(onClick = {
            navController.popBackStack(route = HomeScreen, inclusive = false)
        }) {
            Text(text = "Continue Shopping")
        }
    }

}