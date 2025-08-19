package com.example.fooddelivery.ui.screens.food_detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.navigation.CartScreen
import com.example.fooddelivery.ui.BasicDialog
import com.example.fooddelivery.ui.screens.restaurant_detail.HeaderDetails
import com.example.fooddelivery.ui.screens.restaurant_detail.RestaurantHeader
import com.example.fooddelivery.ui.theme.Primary

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetail(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navController: NavController,
    viewModel: FoodDetailViewModel= hiltViewModel(),
    onItemAddedToCart:()->Unit
) {
    val count= viewModel.quantity.collectAsStateWithLifecycle()
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    val showToast= remember {
        mutableStateOf(false)
    }
    val showErrorDialog= remember {
        mutableStateOf(false)
    }
    val isLoading=remember{
        mutableStateOf(false)
    }
    val showSuccessDialog= remember {
        mutableStateOf(false)
    }
    if(showToast.value){
        Toast.makeText(navController.context,"Item Added To Cart",Toast.LENGTH_SHORT).show()
    }
    when(uiState.value){
        is FoodDetailViewModel.FoodDetailUiState.Success->{
            onItemAddedToCart()
            showSuccessDialog.value=true
            isLoading.value=false
            showErrorDialog.value=false
        }
        is FoodDetailViewModel.FoodDetailUiState.Error->{
            showErrorDialog.value=true
            showSuccessDialog.value=false
            isLoading.value=false
            showToast.value=false
        }
        is FoodDetailViewModel.FoodDetailUiState.Loading->{
            isLoading.value=true
            showSuccessDialog.value=false
            showErrorDialog.value=false
            showToast.value=false
        }
        else->{
            isLoading.value=false
            showSuccessDialog.value=false
            showErrorDialog.value=false
            showToast.value=false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        RestaurantHeader(foodItem.restaurantId,animatedVisibilityScope,foodItem.imageUrl,{
            navController.popBackStack() },{})
        HeaderDetails(animatedVisibilityScope,foodItem.restaurantId,foodItem.name,foodItem.description)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "$"+foodItem.price.toString(), modifier = Modifier.sharedElement(state = rememberSharedContentState("price/${foodItem.id}"),animatedVisibilityScope).padding(start = 10.dp), color = Primary,
                style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.weight(1f))
            ItemCounter(onCounterIncrement = {viewModel.incrementQuantity()}, onCounterDecrement = {viewModel.decrementQuantity()},count.value)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.addToCart(foodItem.restaurantId,foodItem.id!!)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(270.dp),
            enabled = !isLoading.value
        ) {
            AnimatedVisibility(isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
            AnimatedVisibility(!isLoading.value) {
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

    if(showErrorDialog.value){
        ModalBottomSheet(onDismissRequest = {showErrorDialog.value=false}) {
            BasicDialog(msg = "Error", dis = (uiState.value as FoodDetailViewModel.FoodDetailUiState.Error).errMsg?:"" + "Failed To Add To Cart") {
                viewModel.resetUi()
            }
        }
    }
    if(showSuccessDialog.value){
        ModalBottomSheet(onDismissRequest = {showSuccessDialog.value=false}) {
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Text(text="Item Added To Cart", textAlign = TextAlign.Start, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onClick = {
                    showToast.value=true
                    viewModel.resetUi()
                }, colors = ButtonDefaults.buttonColors(Primary)) {
                    Text(text="OK")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onClick = {
                    viewModel.resetUi()
                    navController.navigate(CartScreen)
                },colors = ButtonDefaults.buttonColors(Primary)) {
                    Text(text = "GO TO CART")
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }
        }
    }
}

@Composable
fun ItemCounter(
    onCounterIncrement: () -> Unit,
    onCounterDecrement: () -> Unit,
    count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.1f))
                .clickable { onCounterIncrement.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.plus),
                contentDescription = null,
                modifier = Modifier.size(100.dp).alpha(1.3f)
                    .shadow(6.dp, spotColor = Primary.copy(0.1f), ambientColor = Primary.copy(0.1f))
            )
        }


        Text(
            text = count.toString(),
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.1f))
                .clickable { onCounterDecrement.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.minus),
                contentDescription = null,
                modifier = Modifier.size(100.dp).alpha(1.3f)
                    .shadow(6.dp, spotColor = Primary.copy(0.1f), ambientColor = Primary.copy(0.1f))
            )
        }

    }
}
