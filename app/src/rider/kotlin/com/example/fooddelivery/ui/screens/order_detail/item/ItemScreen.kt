package com.example.fooddelivery.ui.screens.order_detail.item


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.Customer
import com.example.fooddelivery.data.modle.RiderDeliveryData
import com.example.fooddelivery.navigation.CustomerNavType
import com.example.fooddelivery.navigation.RiderOrderDetail
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Blue
import com.example.fooddelivery.ui.theme.Green
import com.example.fooddelivery.ui.theme.Mustard
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.ui.theme.VLBlue
import com.example.fooddelivery.ui.theme.VLGreen
import com.example.fooddelivery.ui.theme.VLOrange
import com.example.fooddelivery.utils.UiState
import kotlin.reflect.typeOf
import java.util.Locale


@Composable
fun ItemScreen(navController: NavController,viewModel: ItemViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        HeaderView({
            navController.popBackStack()
        },"Delivery Items")

        when(uiState.value){
            UiState.Empty -> {
                EmptyState("No Active Orders","Go Back") {
                    navController.popBackStack()
                }
            }
            is UiState.Error -> {
                val dis=(uiState.value as UiState.Error).message
                Error({
                    viewModel.resetUi()
                },"Try Again",dis)
            }
            UiState.Loading ->{
                Loading()
            }
            is UiState.Success<*> -> {
                val list=(uiState.value as UiState.Success).data.data
                if(list.isEmpty()){
                    viewModel.emptyState()
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(list.size){
                        ActiveDeliveryItem(list[it]) { customer, string ->
                            navController.navigate(
                                RiderOrderDetail(orderId = string)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveDeliveryItem(
    deliveryData: RiderDeliveryData,
    onClick: (Customer,String) -> Unit
) {
    Card(
        onClick = { onClick(deliveryData.customer,deliveryData.orderId) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrderStatusChip(status = deliveryData.status)
                Text(
                    text = String.format(Locale.US,"$%.2f", deliveryData.estimatedEarning),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Green
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            AddressRow(
                icon = Icons.Default.HomeWork,
                iconTint = Primary,
                title = deliveryData.restaurant.name,
                subtitle = deliveryData.restaurant.address
            )
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.Gray)
            )
            AddressRow(
                icon = Icons.Default.PersonPin,
                iconTint = Mustard,
                title = deliveryData.customer.city,
                subtitle = deliveryData.customer.addressLine1
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "View Details & Map",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
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
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 18.sp)
        }
    }
}

@Composable
fun OrderStatusChip(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "DELIVERED" -> Pair(VLGreen, Green)
        "OUT_FOR_DELIVERY" -> Pair(VLBlue, Blue) // Blue
        "PREPARING", "ACCEPTED" -> Pair(VLOrange, Orange) // Orange
        else -> Pair(Color.LightGray, Color.Black)
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.replace('_', ' ').uppercase(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}