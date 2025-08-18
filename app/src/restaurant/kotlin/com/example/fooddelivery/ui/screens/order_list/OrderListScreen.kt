package com.example.fooddelivery.ui.screens.order_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Green
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.ui.theme.Red
import com.example.fooddelivery.ui.theme.VLGreen
import com.example.fooddelivery.ui.theme.VLRed
import com.example.fooddelivery.utils.StringUtils
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun  OrderListScreen(navController: NavController, viewModel: OrderListViewModel= hiltViewModel()) {
    val type=viewModel.getOrdersType()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()){
        HeaderView({
            navController.popBackStack()
        },"Orders")
        val pagerState= rememberPagerState(){type.size}
        val coroutineScope= rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Primary
                )
            },
            edgePadding = 8.dp
        ) {
            type.forEachIndexed { index, item ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = Primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        LaunchedEffect(pagerState.currentPage) {
            viewModel.getRestaurantOrderDetails(type[pagerState.currentPage])
        }
        HorizontalPager(pagerState) { page ->

            Column(modifier = Modifier.weight(1f)) {
                    when(uiState){
                        is UiState.Empty -> {
                            EmptyState("No Order List Found","Go Back") {
                                navController.popBackStack()
                            }
                        }
                        is UiState.Error -> {
                            Error(
                                {viewModel.resetUi(type[pagerState.currentPage])},
                                "An Error Occurred",
                                "Please try again"
                            )
                        }
                        is UiState.Loading -> {
                            Loading()
                        }
                        is UiState.Success<*> -> {
                            val orderDetailState = (uiState as UiState.Success<*>).data as OrderListViewModel.OrderDetailState
                            val orderList = orderDetailState.orders?.orders ?: emptyList()
                            if(orderList.isEmpty()){
                                viewModel.emptyState()
                            }
                            LazyColumn {
                            items(
                                count = orderList.size,
                                key = { index -> orderList[index].id }
                            ) { index ->
                                val order = orderList[index]
                                OrderListItem(order = order) {
                                    navController.navigate(OrderDetailScreen(order.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderListItem(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical=8.dp, horizontal = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(order.restaurant.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = order.restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = order.restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = String.format(Locale.US,"$%.2f", order.totalAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${order.items.size} items • ${StringUtils.formatDate(order.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                OrderStatusChip(status = order.status)
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "DELIVERED" -> Pair(VLGreen, Green)
        "CANCELLED", "REJECTED", "DELIVERY_FAILED" -> Pair(VLRed, Red)
        else -> Pair(Primary.copy(alpha = 0.1f), Primary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}