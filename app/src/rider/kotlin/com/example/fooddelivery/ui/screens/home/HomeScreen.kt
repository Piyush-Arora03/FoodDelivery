package com.example.fooddelivery.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.AvailableDeliveriesItem
import com.example.fooddelivery.data.modle.RiderAvailableDeliveriesResponse
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Green
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.ui.theme.Red
import com.example.fooddelivery.ui.theme.VLGreen
import com.example.fooddelivery.ui.theme.VLRed
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController,viewModel: HomeViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.decisionResponse.collectLatest {
            Toast.makeText(navController.context,it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderView({navController.popBackStack()},"Home")
        when(uiState.value){
            is UiState.Empty ->{
                EmptyState("No Available Deliveries","Go to Orders") {

                }
            }
            is UiState.Error -> {
                val msg=(uiState.value as UiState.Error).message
                Error({
                    viewModel.resetUi()
                },"Try Again",msg)
            }
            is UiState.Loading -> {
                Loading()
            }
            is UiState.Success<*> -> {
                val items=(uiState.value as UiState.Success).data.deliveryList!!.data
                if(items.isEmpty()){
                    viewModel.emptyState()
                }
                LazyColumn(modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                    items(items.size,key={items[it].orderId}){
                        AvailableDeliveryItem(items[it],onAccept={
                            viewModel.onAccept(it)
                        },onDecline={
                            viewModel.onDecline(it)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableDeliveryItem(item: AvailableDeliveriesItem,onAccept:(orderId:String) -> Unit,onDecline:(orderId:String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(contentColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.US,"$%.2f",item.estimatedEarning),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Route, contentDescription = "Distance", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = String.format(Locale.US,"%.2f km",item.estimatedEarning),
                        style=MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            AddressRow(
                icon = Icons.Default.HomeWork,
                iconTint = Primary,
                title = item.restaurantName,
                subtitle = item.restaurantAddress
            )
            // Dotted line to represent the route
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.Gray)
            )
            AddressRow(
                icon = Icons.Default.PinDrop,
                iconTint = Color(0xFFD32F2F), // Red for customer pin
                title = "Customer Location",
                subtitle = item.customerAddress
            )

            Spacer(modifier = Modifier.height(20.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(ButtonDefaults.shape)
                        .height(ButtonDefaults.MinHeight)
                        .background(VLRed)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .weight(1f)
                        .clickable{
                            onDecline(item.orderId)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Decline",
                        color = Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(ButtonDefaults.shape)
                        .height(ButtonDefaults.MinHeight)
                        .background(VLGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .weight(1f)
                        .clickable{
                            onAccept(item.orderId)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Accept",
                        color = Green,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

    @Composable
    private fun AddressRow(
        icon: ImageVector,
        iconTint: Color,
        title: String,
        subtitle: String
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 18.sp)
            }
        }
    }