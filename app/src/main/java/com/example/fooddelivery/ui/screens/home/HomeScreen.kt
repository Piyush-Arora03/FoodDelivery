package com.example.fooddelivery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.Category
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