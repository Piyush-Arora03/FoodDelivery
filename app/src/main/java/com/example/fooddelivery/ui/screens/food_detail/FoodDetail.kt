package com.example.fooddelivery.ui.screens.food_detail

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.ui.screens.restaurant_detail.HeaderDetails
import com.example.fooddelivery.ui.screens.restaurant_detail.RestaurantHeader
import com.example.fooddelivery.ui.theme.Orange

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetail(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navController: NavController
) {
    val count= remember {
        mutableStateOf(1)
    }
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        RestaurantHeader(foodItem.restaurantId,animatedVisibilityScope,foodItem.imageUrl,{
            navController.popBackStack() },{})
        HeaderDetails(animatedVisibilityScope,foodItem.restaurantId,foodItem.name,foodItem.description)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "$"+foodItem.price.toString(), modifier = Modifier.sharedElement(state = rememberSharedContentState("price/${foodItem.id}"),animatedVisibilityScope).padding(start = 10.dp), color = Orange,
                style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.padding(horizontal = 30.dp))
            Image(painter = painterResource(R.drawable.plus), contentDescription = null, modifier = Modifier.size(80.dp).clickable {
                count.value++
            })
            Text(text = count.value.toString(), modifier = Modifier, color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Start)
            Image(painter = painterResource(R.drawable.minus), contentDescription = null, modifier = Modifier.size(100.dp).clickable {
                if(count.value>1) count.value--
            })
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(270.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Image aligned to start
                Image(
                    painter = painterResource(R.drawable.cart_bag),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                )

                // Text aligned center
                Text(
                    text = "ADD TO CART",
                    modifier = Modifier.align(Alignment.Center).padding(vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp
                )
            }
        }
    }
}