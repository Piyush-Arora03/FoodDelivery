package com.example.fooddelivery.ui.screens.home

import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.Category
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.navigation.RestaurantDetailScreen
import com.example.fooddelivery.ui.FoodHubTextFiled
import com.example.fooddelivery.ui.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // This is the root component for a side navigation drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(onItemClick = { /* Handle drawer item clicks */ })
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(onMenuClick = {
                    scope.launch { drawerState.open() }
                })
            },
            // Note: The bottom bar should be provided by your MainActivity's Scaffold
            // to persist across screens. This is a placeholder for visual layout.
            bottomBar = {
                // Your BottomBar from MainActivity would go here
            }
        ) { paddingValues ->
            when (uiState) {
                is HomeViewModel.HomeScreenState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No restaurants found.")
                    }
                }
                is HomeViewModel.HomeScreenState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${(uiState as HomeViewModel.HomeScreenState.Error).message}")
                    }
                }
                is HomeViewModel.HomeScreenState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeViewModel.HomeScreenState.Success -> {
                    // LazyColumn for the main scrollable content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Header Text
                        item {
                            Text(
                                text = "What would you like\nto order",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        // Search Bar
                        item {
                            var searchText by remember { mutableStateOf("") }
                            FoodHubTextFiled(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                placeholder = { Text("Find for food or restaurant...") },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_search_24),
                                        contentDescription = "Filter",
                                        tint = Primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }

                        // Categories Section
                        item {
                            CategoryList(
                                categories = viewModel.categories,
                                onCategorySelected = { /* Handle category selection */ }
                            )
                        }

                        // Featured Restaurants Section
                        item {
                            RestaurantSection(
                                title = "Featured Restaurants",
                                restaurants = viewModel.restaurants,
                                onRestaurantClick = { restaurant ->
                                    navController.navigate(RestaurantDetailScreen(restaurant.name, restaurant.imageUrl, restaurant.id))
                                }
                            )
                        }

                        // Popular Items Section (using same data for example)
                        item {
                            RestaurantSection(
                                title = "Popular Items",
                                restaurants = viewModel.restaurants.shuffled(), // Mix it up for demo
                                onRestaurantClick = { /* ... */ }
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
fun HomeTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(text = "Deliver to", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = "4102 Pretty View Lane",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            AsyncImage(
                model = "https://i.pravatar.cc/150?u=a042581f4e29026704d", // Placeholder avatar
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
        },
        windowInsets = WindowInsets(0.dp)
    )
}

@Composable
fun CategoryList(categories: List<Category>, onCategorySelected: (Category) -> Unit) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { selectedCategory = it }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryItem(category: Category, isSelected: Boolean, onClick: (Category) -> Unit) {
    // Define colors at the top for clarity
    val backgroundColor = if (isSelected) Primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Card(
        onClick = { onClick(category) },
        modifier = Modifier
            .width(75.dp)
            .height(140.dp),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (isSelected) 8.dp else 0.dp,
                        shape = CircleShape
                    )
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = category.name,
                    modifier = Modifier.size(32.dp),
                )
            }

            // The modified Text composable
            Text(
                text = category.name,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun RestaurantSection(
    title: String,
    restaurants: List<Restaurant>,
    onRestaurantClick: (Restaurant) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "View All >", style = MaterialTheme.typography.bodyMedium, color = Primary, modifier = Modifier.clickable { })
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(restaurants) { restaurant ->
                RestaurantItem(restaurant = restaurant, onClick = { onRestaurantClick(restaurant) })
            }
        }
    }
}

@Composable
fun RestaurantItem(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp)) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // You can add rating and favorite icons here as in the Figma design
            }
            Column(Modifier.padding(12.dp)) {
                Text(text = restaurant.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Free delivery", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("10-15 mins", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AppDrawerContent(onItemClick: (String) -> Unit) {
    ModalDrawerSheet(
        windowInsets = WindowInsets(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 40.dp)
        ) {
            AsyncImage(
                model = "https://i.pravatar.cc/150?u=a042581f4e29026704d", // Placeholder avatar
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
            )
            Text("Farion Wick", modifier = Modifier.padding(16.dp))
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        NavigationDrawerItem(
            label = { Text("My Orders") },
            selected = false,
            onClick = { /*TODO*/ },
            icon = {Icon(
                painterResource(R.drawable.ic_order),
                contentDescription = "orders",
                modifier = Modifier.size(36.dp),
                tint = Primary
            )}
        )
    }
}