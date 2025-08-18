package com.example.fooddelivery.ui.screens.menu.list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.data.modle.FoodItemListResponse
import com.example.fooddelivery.ui.EmptyState
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.utils.UiState
import java.util.Locale
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
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderView({
                    navController.popBackStack()
                },"Menu Items")

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = list.size,
                        key ={index -> list[index].id}
                    ){
                        MenuListItem(list[it], onClick = {})
                    }
                }
            }
        }
    }
}

@Composable
fun MenuListItem(item: FoodItem,onClick:() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(true){
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column(modifier = Modifier.fillMaxWidth()
                .padding(8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Primary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = String.format(Locale.US,"$%.2f", item.price),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}