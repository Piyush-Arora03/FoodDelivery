package com.example.fooddelivery.ui.screens.home

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.navigation.RestaurantMenuItem
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController,homeViewModel: HomeViewModel= hiltViewModel()) {
    val uiState=homeViewModel.uiState.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) {
        homeViewModel.navigationEvent.collectLatest {
            when(it){
                is HomeViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is HomeViewModel.NavigationEvent.NavigateToOrderDetail -> {
                    navController.navigate(OrderDetailScreen(it.orderId))
                }
            }
        }
    }
    val TAG="RestaurantHome"
    LaunchedEffect(Unit) {
        homeViewModel.getRestaurant()
    }

    Column(modifier = Modifier
        .fillMaxSize()) {
        HeaderView(
            {
                navController.popBackStack()
            },
            name = "Home",
        )
        when(uiState){
            is HomeViewModel.UiState.Loading -> {
                Loading()
            }
            is HomeViewModel.UiState.Error -> {
                Error(
                    {homeViewModel.resetUi()},
                    "An Error Occurred",
                    "Please try again"
                )
            }
            is HomeViewModel.UiState.Success -> {
                Log.d(TAG,uiState.restaurant.toString())
                Column {
                    RestaurantHeader(restaurant = uiState.restaurant)
                    Spacer(modifier = Modifier.padding(12.dp))
                    MenuActionsSection({},{
                        navController.navigate(RestaurantMenuItem(uiState.restaurant.id))
                    })
                }
            }
        }
    }


}

@Composable
fun RestaurantHeader(restaurant: Restaurant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(restaurant.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 400f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MenuActionsSection(
    onAddMenuItem: () -> Unit,
    onViewMenu: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Menu Management",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        ActionRowItem(
            title = "Add New Item",
            description = "Create a new dish for your menu",
            icon = Icons.Default.AddCircle,
            onClick = onAddMenuItem
        )
        ActionRowItem(
            title = "View Full Menu",
            description = "See and edit all your existing items",
            icon = Icons.AutoMirrored.Filled.ListAlt,
            onClick = onViewMenu
        )
    }
}

@Composable
fun ActionRowItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Primary.copy(alpha = 0.8f))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}