package com.example.fooddelivery.ui.screens.menu.list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.data.modle.FoodItemListResponse
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MenuListScreen(restaurantId:String,navController: NavController,viewModel: MenuListViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                MenuListViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMenuItems(restaurantId)
    }

    when(uiState.value){
        is UiState.Empty -> {
            EmptyState("No Menu Item To Show") {
                navController.popBackStack()
            }
        }
        is UiState.Error -> {
            Log.d("resetUi","in getMenuItem error...")
            Error({viewModel.navigateBack()},
                (uiState.value as UiState.Error).message,
                "Some Error Occurred \n Please Try Again")
        }
        is UiState.Loading -> {
            Log.d("resetUi","in getMenuItem loading...")
            Loading()
        }
        is UiState.Success<*> -> {
            val data=(uiState.value as UiState.Success<*>).data as FoodItemListResponse
            val list=data.foodItems
            if(list.isEmpty()){
                viewModel.emptyState()
            }
            LazyColumn {
                items(count = list.size,
                    key ={index->  list[index].id}) {
                    MenuListItem(list[it])
                }
            }
        }
    }
}

@Composable
fun MenuListItem(item: FoodItem) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp).
    clip(RoundedCornerShape(8.dp))) {
        Text(text = item.id)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = item.name)
    }
}