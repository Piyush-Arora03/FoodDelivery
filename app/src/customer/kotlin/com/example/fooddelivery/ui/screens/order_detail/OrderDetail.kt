package com.example.fooddelivery.ui.screens.order_detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.data.modle.OrderItem
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.screens.orders.OrdersListViewModel
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderDetail(
    navController: NavController,
    orderId: String,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    Log.d("OrderDetail",uiState.toString())
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                OrderDetailViewModel.OrderDetailNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getOrderDetails(orderId)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderView( {navController.popBackStack()} ,"Order Detail")

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                OrderDetailContent(order = uiState.data)
            }

            is UiState.Error -> {
                OnUiStateError(
                    onClick = { viewModel.getOrderDetails(orderId) },
                    text = viewModel.errMsg
                )
            }
            is UiState.Empty -> {
                Toast.makeText(navController.context,"Empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun OrderDetailContent(order: Order, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Summary Section
        item {
            OrderSummaryCard(order)
        }

        // Items List Section
        item {
            Text(
                text = "Your Order",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(order.items) { item ->
            OrderItemRow(item)
            Divider()
        }

        // Price Details Section
        item {
            PriceDetailsCard(order)
        }

        // Spacer at the bottom
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OrderSummaryCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow(label = "Order ID", value = order.id)
            InfoRow(label = "Status", value = order.status, valueColor = Primary, isBold = true)
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${item.quantity}x", style = MaterialTheme.typography.bodyLarge, color = Primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = item.menuItemName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Text(text = "$${"%.2f".format(6.00 * item.quantity)}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun PriceDetailsCard(order: Order) {
    val subtotal = order.totalAmount
    val deliveryFee=20.00
    val total = subtotal+deliveryFee

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow(label = "Subtotal", value = "$${"%.2f".format(subtotal)}")
            InfoRow(label = "Delivery Fee", value = "$${"%.2f".format(deliveryFee)}")
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            InfoRow(label = "Total", value = "$${"%.2f".format(total)}", isBold = true)
        }
    }
}


@Composable
fun OrderDetailHeaderView(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(R.drawable.back_button),
                contentDescription = "Back",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            text = "Order Detail",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun OnUiStateError(onClick: () -> Unit, text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { onClick.invoke() },
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Retry", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = fontWeight
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = fontWeight
        )
    }
}