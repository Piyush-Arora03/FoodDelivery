package com.example.fooddelivery.ui.screens.restaurant_detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.navigation.FoodDetailScreen
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
     name:String,
     imageUrl:String,
     restaurantId:String,
     navController: NavController,
     viewModel: RestaurantViewModel= hiltViewModel()
) {
    val sheetState= rememberModalBottomSheetState()
    val scope= rememberCoroutineScope()
    val showDialog= remember {
        mutableStateOf(false)
    }
    val uiState=viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getFoodItem(restaurantId)
    }
    LazyColumn {
        item {
            RestaurantHeader(
                restaurantId,
                animatedVisibilityScope = animatedVisibilityScope,
                imageUrl = imageUrl,
                onFavButtonClick = {},
                onBackButtonClick = { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.padding(10.dp))
            HeaderDetails(
                animatedVisibilityScope=animatedVisibilityScope,
                restaurantId=restaurantId,
                name=name
            )
        }

        when (uiState.value) {
            is RestaurantViewModel.RestaurantUiState.Success -> {
                val foodItems = (uiState.value as RestaurantViewModel.RestaurantUiState.Success).data
                items(foodItems.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (item in rowItems) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)  // Equal spacing
                                    .padding(4.dp)
                            ) {
                                FoodItems(item = item, onFavButtonClick = {}, animatedVisibilityScope = animatedVisibilityScope, onClick =
                                    {
                                        navController.navigate(
                                            FoodDetailScreen(foodItem=it))
                                    })
                            }
                        }

                        // Fill empty space if row has only one item
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            is RestaurantViewModel.RestaurantUiState.Error -> {
                showDialog.value = true
            }

            RestaurantViewModel.RestaurantUiState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Text(text = "Loading..", fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }

            RestaurantViewModel.RestaurantUiState.Nothing -> {
                // Do nothing
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantHeader(
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    imageUrl: String,
    onBackButtonClick:()->Unit,
    onFavButtonClick:()->Unit
) {
                Box(modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))){
                    AsyncImage(model = imageUrl.ifBlank { painterResource(R.drawable.ic_google) }, contentDescription = null,
                        modifier = Modifier.height(200.dp).fillMaxWidth().sharedElement(state = rememberSharedContentState("image/${restaurantId}"),animatedVisibilityScope)
                        , contentScale = ContentScale.Crop)
                    IconButton(onClick = onBackButtonClick, modifier = Modifier.align(Alignment.TopStart)
                        .padding(8.dp)) {
                        Image(painter = painterResource(R.drawable.back_button),contentDescription = null,
                            modifier = Modifier.fillMaxSize().scale(1f))
                    }
                    IconButton(onClick = onFavButtonClick, modifier = Modifier.align(Alignment.TopEnd)
                        .padding(8.dp)) {
                        Image(painter = painterResource(R.drawable.favourite),contentDescription = null,
                            modifier = Modifier.fillMaxSize().scale(1f))
                    }
                }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HeaderDetails(
    animatedVisibilityScope: AnimatedVisibilityScope,
    restaurantId: String,name: String,description:String=LoremIpsum(50).values.first()){
    Column(modifier = Modifier.fillMaxWidth()
        .wrapContentHeight()) {
    Text(text = name, textAlign = TextAlign.Start, modifier = Modifier.padding(start = 8.dp)
        .sharedElement(state = rememberSharedContentState("name/$restaurantId"),animatedVisibilityScope)
        , style = TextStyle(
            fontSize = 30.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.SemiBold
        )
    )
    Spacer(modifier = Modifier.padding(vertical = 4.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(painter = painterResource(R.drawable.star), contentDescription = null, modifier = Modifier.size(20.dp).padding(start = 8.dp))
        Text(text = " 4.5 (30+) ", modifier = Modifier.padding(start = 4.dp), fontSize = 10.sp)
        Text(text = " See Review >", modifier = Modifier.padding(start = 4.dp), fontSize = 10.sp, color = Primary)
    }
    Spacer(modifier = Modifier.padding(4.dp))
    Text(text = description, modifier = Modifier.padding(8.dp).sharedElement(state = rememberSharedContentState("description/${restaurantId}"),animatedVisibilityScope)
        , style = TextStyle(
            fontSize = 10.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal
        ),
        color = Color.Gray
    )
}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItems(item:FoodItem,onFavButtonClick:()->Unit,onClick:(FoodItem)->Unit,animatedVisibilityScope: AnimatedVisibilityScope){
    Column(modifier = Modifier.padding(8.dp)
        .clip(RoundedCornerShape(16.dp))
        .clickable { onClick(item) }) {
        Box(modifier = Modifier.fillMaxWidth()){
            AsyncImage(model = item.imageUrl, contentDescription = null,
                modifier = Modifier.height(130.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .sharedElement(state = rememberSharedContentState("image/${item.id}"), animatedVisibilityScope = animatedVisibilityScope),
                contentScale = ContentScale.Crop)
            Text(text = "$ "+item.price, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .align(Alignment.TopStart)
                .sharedElement(state = rememberSharedContentState("price/${item.id}"),animatedVisibilityScope), fontSize = 12.sp)
            IconButton(onClick = onFavButtonClick, modifier = Modifier.align(Alignment.TopEnd)) {
                Image(painter = painterResource(R.drawable.favourite),contentDescription = null,
                    modifier = Modifier.fillMaxSize())
            }
            Row(modifier = Modifier.align(Alignment.BottomStart)
                .padding(top=8.dp, end = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("4.5", modifier = Modifier.padding(end = 4.dp, start = 4.dp), fontSize = 12.sp, color = Color.Black)
                Image(painter = painterResource(R.drawable.star), contentDescription = null, modifier = Modifier.size(10.dp))
                Text("(25+)", modifier = Modifier.padding(start = 4.dp, end = 4.dp), fontSize = 10.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Text(text = item.name, fontSize = 14.sp, color = Color.Black, modifier = Modifier.sharedElement(state = rememberSharedContentState(
         "name/${item.id}"),animatedVisibilityScope))
        Spacer(modifier = Modifier.padding(2.dp))
        Text(text = item.description, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.sharedElement(state = rememberSharedContentState("description/${item.id}"),animatedVisibilityScope))
        Spacer(modifier = Modifier.padding(4.dp))
    }
}