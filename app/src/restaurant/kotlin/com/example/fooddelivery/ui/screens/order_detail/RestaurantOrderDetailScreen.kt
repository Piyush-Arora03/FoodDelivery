package com.example.fooddelivery.ui.screens.order_detail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RestaurantOrderDetailScreen(
    orderId:String,
    navController: NavController,
    viewModel: RestaurantOrderDetailViewModel= hiltViewModel()) {

    LaunchedEffect(orderId) {
        viewModel.getOrderDetails(orderId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collectLatest {
                when(it){
                    is RestaurantOrderDetailViewModel.NavigationEvent.NavigateBack -> {
                        navController.popBackStack()
                    }
                    is RestaurantOrderDetailViewModel.NavigationEvent.ShowPopUp -> {
                        viewModel.resetUi(orderId)
                        Toast.makeText(navController.context,"Status Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val uiState=viewModel.uiState.collectAsStateWithLifecycle()
        when(uiState.value){
            is UiState.Empty -> TODO()
            is UiState.Error -> TODO()
            is UiState.Loading -> {
                Loading()
            }
            is UiState.Success<*> -> {
                val orderListState= (uiState.value as UiState.Success<*>).data as RestaurantOrderDetailViewModel.OrderDetailState
                val order=orderListState.selectedOrder!!
                Text(text = order.id)
                order.items.forEach {
                    Column(modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)) {
                        Text(text = it.menuItemId)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = it.menuItemName)
                    }
                }
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    viewModel.types.forEach {
                        Button(
                            onClick = {
                                viewModel.updateOrderStatus(orderId,it)
                            },
                            enabled = order.status!=it
                        ){
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }

}