package com.example.fooddelivery.ui.screens.order_detail.detail

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.Customer
import com.example.fooddelivery.data.modle.OrderItem
import com.example.fooddelivery.data.modle.RiderDeliveryItem
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.utils.OrderStatusUtils
import com.example.fooddelivery.utils.UiState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RiderOrderDetailScreen(orderId:String, navController: NavController,viewModel: RiderOrderDetailViewModel= hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.getOrderDetails(orderId)
    }

    LaunchedEffect(Unit) {
        val event=viewModel.navigationEvent.collectLatest {
            when(it){
                is RiderOrderDetailViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is RiderOrderDetailViewModel.NavigationEvent.ShowPopUp -> {
                    Toast.makeText(navController.context,it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        HeaderView({
            navController.popBackStack()
        }, "Order Details")
        when (uiState.value) {
            UiState.Empty -> {
                EmptyState("No Order Details Are Available", "Go Back") {
                    navController.popBackStack()
                }
            }

            is UiState.Error -> {
                val dis = (uiState.value as UiState.Error).message
                Error({
                    viewModel.resetUi(orderId)
                }, "Try Again", dis)
            }

            UiState.Loading -> {
                Loading()
            }

            is UiState.Success<*> -> {
                val deliveryData = (uiState.value as UiState.Success).data.selectedOrder!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- 1. Conditional Map Section ---
                    item {
                        AnimatedVisibility(
                            visible = deliveryData.status == "OUT_FOR_DELIVERY",
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically(),
                        ) {
                            val messages=viewModel.message.collectAsStateWithLifecycle("")
                            Log.d("Messages",messages.value)
                            MapPlaceholder()
                        }
                    }

                    // --- 2. New Status Update Card ---
                    item {
                        StatusUpdateActionCard(
                            currentStatus = deliveryData.status,
                            onUpdateStatus ={ viewModel.updateOrderStatus(orderId,it)}
                        )
                    }

                    // --- 3. Location and Order Cards (remain the same) ---
                    item {
                        LocationCard(
                            title = "Pickup From",
                            name = deliveryData.restaurant.name,
                            address = deliveryData.restaurant.address,
                            icon = Icons.Default.HomeWork,
                            isHighlighted = deliveryData.status !in listOf(
                                "OUT_FOR_DELIVERY",
                                "DELIVERED"
                            ),
                            lat = deliveryData.restaurant.latitude,
                            long = deliveryData.restaurant.longitude
                        )
                    }
//                    item {
//                        LocationCard(
//                            title = "Deliver To",
//                            address = customer.addressLine1,
//                            icon = Icons.Default.PersonPin,
//                            isHighlighted = deliveryData.status == "OUT_FOR_DELIVERY",
//                            lat = customer.latitude,
//                            long = customer.longitude
//                        )
//                    }
                    item {
                        OrderSummaryCard(
                            items = deliveryData.items,
                            totalAmount = deliveryData.totalAmount
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun MapPlaceholder() {
    Column() {
        val cameraPositionState=rememberCameraPositionState()
        GoogleMap(
            modifier = Modifier.fillMaxWidth().height(500.dp),
            cameraPositionState=cameraPositionState
        ) {

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusUpdateActionCard(
    currentStatus: String,
    onUpdateStatus: (String) -> Unit
) {
    // This logic determines which actions are available to the rider.
    val availableActions = getNextAvailableStatuses(currentStatus)

    if (availableActions.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Next Step",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Current Status: ${formatStatus(currentStatus)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                // FlowRow automatically wraps buttons to the next line if they don't fit.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableActions.forEach { action ->
                        Button(onClick = { onUpdateStatus(action) }) {
                            Text(text = formatStatus(action))
                        }
                    }
                }
            }
        }
    }
}

// This helper function contains the business logic for the delivery flow.
private fun getNextAvailableStatuses(currentStatus: String): List<String> {
    return when (currentStatus.uppercase()) {
        "ASSIGNED" -> listOf("OUT_FOR_DELIVERY")
        "OUT_FOR_DELIVERY" -> listOf("DELIVERED", "DELIVERY_FAILED")
        else -> emptyList() // No actions if delivered, cancelled, etc.
    }
}

// Helper function to make status strings look nice
private fun formatStatus(status: String): String {
    return status.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }
}

@Composable
private fun LocationCard(
    title: String,
    name:String?=null,
    address: String,
    icon: ImageVector,
    isHighlighted: Boolean,
    lat: Double,
    long: Double
) {
    // Get the current context, which is needed to launch the maps Intent
    val context = LocalContext.current

    // Create the URI for the map intent. "geo:" is a standard URI to open map apps.
    val mapIntentUri = Uri.parse("geo:$lat,$long?q=$lat,$long")

    // Create the actual Intent. ACTION_VIEW tells the system to open the content.
    val mapIntent = remember { Intent(Intent.ACTION_VIEW, mapIntentUri) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        // Add a primary-colored border if this card is the current, active step
        border = if (isHighlighted) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The icon (e.g., for restaurant or customer)
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )

            // The column for text details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                if(name!=null)Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // The button to launch the maps app
            Button(onClick = { context.startActivity(mapIntent) }) {
                Text("Navigate")
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(items: List<OrderItem>, totalAmount: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${item.quantity} x", modifier = Modifier.width(40.dp))
                    Text(item.menuItemName, modifier = Modifier.weight(1f))
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Payment to Collect", style = MaterialTheme.typography.bodyLarge)
                Text(String.format("₹%.2f", totalAmount), fontWeight = FontWeight.Bold)
            }
        }
    }
}
