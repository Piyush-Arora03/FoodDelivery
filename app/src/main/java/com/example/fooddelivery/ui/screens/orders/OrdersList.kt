package com.example.fooddelivery.ui.screens.orders

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.ui.BasicDialog
import com.example.fooddelivery.ui.screens.address_list.OnUiStateError
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersList(navController: NavController,viewModel: OrdersListViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsStateWithLifecycle().value
    val showErrorDialog= remember {
        mutableStateOf(false)
    }
    when(uiState){
        is OrdersListViewModel.UiState.Error -> {
           OnUiStateError(onClick = {viewModel.getOrderList()},"Retry")
        }
        is OrdersListViewModel.UiState.Loading -> {
            Column(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(text = "Loading...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        is OrdersListViewModel.UiState.Nothing -> {
            showErrorDialog.value=false
        }
        is OrdersListViewModel.UiState.OrderList -> {
            val orderList=(uiState as OrdersListViewModel.UiState.OrderList).orderList
            LazyColumn {
                items(orderList.size){
                    OrderListItem(orderList[it],onClick={viewModel.navigateToOrderDetailScreen(orderList[it])})
                }
            }
        }
    }
    if(showErrorDialog.value){
        ModalBottomSheet(onDismissRequest = {showErrorDialog.value=false
        viewModel.resetUi()}) {
            BasicDialog(msg=(uiState as OrdersListViewModel.UiState.Error).errMsg,dis=viewModel.errMsg,onClick = { viewModel.resetUi() })
        }
    }
}

@Composable
fun OrderListItem(order: Order, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = true)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order.restaurant.imageUrl.ifBlank { painterResource(R.drawable.ic_google) },
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .shadow(6.dp, RoundedCornerShape(8.dp), clip = true)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${order.items.size} Items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                    , fontFamily = poppinsFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = order.restaurant.name,
                    style = MaterialTheme.typography.titleMedium
                    , fontFamily = poppinsFontFamily
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = order.id,
                style = MaterialTheme.typography.titleSmall,
                color = Orange,
                maxLines = 1,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 24.dp)
                , fontFamily = poppinsFontFamily
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Status", style = MaterialTheme.typography.titleMedium, color = Color.Gray,
            modifier = Modifier.padding(start = 12.dp), fontFamily = poppinsFontFamily)
        Text(text = order.status, style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 12.dp), fontFamily = poppinsFontFamily)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(26.dp), clip = false, spotColor = Orange, ambientColor = Orange)
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Track Order",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = poppinsFontFamily,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}
