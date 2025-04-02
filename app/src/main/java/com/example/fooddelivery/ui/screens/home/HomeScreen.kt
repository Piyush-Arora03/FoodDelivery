package com.example.fooddelivery.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.Category
import com.example.fooddelivery.data.modle.Restaurant
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.poppinsFontFamily


@Composable
fun HomeScreen(navController: NavController,viewModel: HomeViewModel= hiltViewModel()) {
    Column(modifier = Modifier
        .fillMaxSize()) {
        val uiState=viewModel.uiState.collectAsState()
        when(uiState.value){
            is HomeViewModel.HomeScreenState.Success->{
                CategoryList(category = viewModel.categories) {
                    navController.navigate("category/${it.id}")
                }
                Spacer(modifier = Modifier.padding(15.dp))
                Box(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "FeaturedRestaurants", style = TextStyle(
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                            textAlign = TextAlign.Start)
                        Text(text = "View All >", style = TextStyle(
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                            color = Orange,
                            textAlign = TextAlign.End,)
                    } }
                Spacer(modifier = Modifier.padding(10.dp))
                RestaurantList(restaurants = viewModel.restaurants) {
                    navController.navigate("restaurant/${it.id}")
                }
            }
            is HomeViewModel.HomeScreenState.Error->{
                Text(text = "Error")
            }
            is HomeViewModel.HomeScreenState.Empty->{
                Text(text = "Empty")
            }
            is HomeViewModel.HomeScreenState.Loading->{
                Text(text = "Loading")
            }
        }
    }
}

@Composable
fun CategoryList(category: List<Category>,onCategorySelected:(Category)->Unit){
    LazyRow {
        items(category){
            CategoryItem(category=it, onCategorySelected = onCategorySelected)
        }
    }
}

@Composable
private fun CategoryItem(category: Category,onCategorySelected: (Category) -> Unit) {
    Column(modifier =Modifier
        .padding(8.dp)
        .shadow(6.dp, shape = RoundedCornerShape(45.dp))
        .width(60.dp)
        .height(100.dp)
        .clip(RoundedCornerShape(45.dp))
        .background(Color.White)
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround) {
        AsyncImage(model = category.imageUrl.ifBlank { painterResource(R.drawable.ic_google) }, contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Inside)
        Text(text = category.name, style = TextStyle(
            fontSize = 10.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        ),
            maxLines = 2)
    }
}
@Composable
fun RestaurantList(restaurants: List<Restaurant>,onClick:(Restaurant)->Unit){
    LazyRow {
        items(restaurants){
            RestaurantItem(restaurant = it,onClick)
        }
    }
}


@Composable
fun RestaurantItem(restaurant: Restaurant, onClick: (Restaurant) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick(restaurant) }
            .background(Color.White),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            // Image with overlaying elements
            Box(modifier = Modifier.height(130.dp)) {
                AsyncImage(
                    model = restaurant.imageUrl.ifBlank { painterResource(R.drawable.ic_google) },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )

                // Rating Badge
                Row(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.star), contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "4.5",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = " (25+)",
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Light)
                    )
                }

                // Favorite Icon
                Image(
                    painter = painterResource(R.drawable.favourite),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Restaurant Name with Verified Icon
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = restaurant.name,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Image(painter = painterResource(R.drawable.blue_tick), contentDescription = null, modifier = Modifier.size(12.dp))
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // Delivery Info
            Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.free_delivery), contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Free delivery",
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.Gray)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Image(painter = painterResource(R.drawable.delivery_time), contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "10-15 mins",
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.Gray)
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}
