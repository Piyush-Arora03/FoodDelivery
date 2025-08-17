package com.example.fooddelivery.ui.screens.order_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.Order
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.launch

@Composable
fun  OrderListScreen(navController: NavController, viewModel: OrderListViewModel= hiltViewModel()) {
    val type=viewModel.getOrdersType()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()){
        HeaderView({
            navController.popBackStack()
        },"Orders Detail")
        val pagerState= rememberPagerState(){type.size}
        val coroutineScope= rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            type.forEachIndexed { index , item ->
                Text(text = item, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        })
            }
        }
        LaunchedEffect(pagerState.currentPage) {
            viewModel.getRestaurantOrderDetails(type[pagerState.currentPage])
        }
        HorizontalPager(pagerState) { page ->

            Column(modifier = Modifier.weight(1f)) {
                Text(text = type[page], modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
                    when(uiState){
                        is UiState.Empty -> {
                            EmptyState("No Order List Found") {
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

@Composable
fun OrderListItem(order: Order, onClick:()->Unit){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable{
            onClick()
        }) {
        Text(text=order.id)
    }
}