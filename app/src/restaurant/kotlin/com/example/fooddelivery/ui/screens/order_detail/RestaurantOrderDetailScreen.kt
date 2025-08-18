package com.example.fooddelivery.ui.screens.order_detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.modle.OrderItem
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
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
            is UiState.Empty -> {
                EmptyState(message = "No order details found") {
                    navController.popBackStack()
                }
            }
            is UiState.Error -> {
                Error(
                    {viewModel.resetUi(orderId)},
                    "An Error Occurred",
                    "Please try again"
                )
            }
            is UiState.Loading -> {
                Loading()
            }
            is UiState.Success<*> -> {
                val orderListState= (uiState.value as UiState.Success<*>).data as RestaurantOrderDetailViewModel.OrderDetailState
                val order=orderListState.selectedOrder!!
                OrderItemsCard(order)
                Spacer(modifier = Modifier.padding(16.dp))
                StatusUpdateCard(order.status,viewModel.types
                    .filter { it!=order.status }) {
                    viewModel.updateOrderStatus(orderId,it)
                }
            }
        }
    }

}

@Composable
private fun OrderItemsCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Items in Order #${order.id.take(6)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            order.items.forEach { item ->
                OrderItemRow(item = item)
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.quantity} x",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary // Your app's blue
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.menuItemName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusUpdateCard(
    currentStatus: String,
    availableStatus: List<String>,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Update Order Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Current Status: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatStatus(currentStatus),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableStatus.forEach { status ->
                    if (status == currentStatus) {
                        Button(onClick = {}, enabled = false) {
                            Text(text = formatStatus(status))
                        }
                    } else {
                        OutlinedButton(onClick = { onUpdateStatus(status) }) {
                            Text(text = formatStatus(status))
                        }
                    }
                }
            }
        }
    }
}

private fun formatStatus(status: String): String {
    return status.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }
}