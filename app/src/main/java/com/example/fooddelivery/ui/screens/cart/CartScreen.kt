package com.example.fooddelivery.ui.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.R
import com.example.fooddelivery.data.modle.CartItem
import com.example.fooddelivery.data.modle.CheckoutDetails
import com.example.fooddelivery.ui.BasicDialog
import com.example.fooddelivery.ui.screens.food_detail.FoodDetailViewModel
import com.example.fooddelivery.ui.screens.food_detail.ItemCounter
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.poppinsFontFamily
import com.example.fooddelivery.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController,viewModel: CartViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsState()
    var showErrorDialog= remember {
        mutableStateOf(false)
    }
    var isCartEmpty= remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        val navigationEvent=viewModel.navigationEvent.collectLatest {
            when(it){
                CartViewModel.CartEvent.OnCheckOut -> TODO()
                CartViewModel.CartEvent.OnQuantityUpdateError,
                CartViewModel.CartEvent.OnRemoveError,
                CartViewModel.CartEvent.ShowErrorDialog -> {
                    showErrorDialog.value=true
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CartHeaderView { navController.popBackStack() }
        when (uiState.value) {
            is CartViewModel.CartUiState.Success -> {
                val data = (uiState.value as CartViewModel.CartUiState.Success).cartResponse
                LazyColumn {
                    items(data.items) {
                        CartItemView(it,
                            onIncrement = {viewModel.incrementCounter(it)},
                            onDecrement = {viewModel.decrementCounter(it)},
                            onRemove = {viewModel.removeItem(it)})
                    }
                    item {
                        if(data.items.isEmpty()){
                             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                 Text(text = "Cart is empty")
                             }
                        }
                        else{
                            Spacer(modifier = Modifier.padding(10.dp))
                            CheckoutDetailView(data.checkoutDetails)
                        }
                    }
                }
            }

            is CartViewModel.CartUiState.Error -> {
                val errMsg=(uiState.value as CartViewModel.CartUiState.Error).error
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errMsg, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
                    Button(onClick = {},Modifier
                        .clip(RoundedCornerShape(16.dp))
                        , colors = ButtonDefaults.buttonColors(Orange)) {
                        Text(text="RETRY", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            CartViewModel.CartUiState.Loading -> {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }
            CartViewModel.CartUiState.Nothing -> TODO()
        }
    }
    if(showErrorDialog.value){
        ModalBottomSheet(onDismissRequest = {showErrorDialog.value=false}) {
            BasicDialog(msg = viewModel.errMsg, dis =viewModel.errDes ) {
                viewModel.resetUi()
            }
        }
    }
}

@Composable
fun CartHeaderView(onBack:()->Unit){
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = {onBack()}, modifier = Modifier.padding(8.dp).align(Alignment.CenterStart)) {
            Image(painter = painterResource(R.drawable.back_button), contentDescription = null, modifier = Modifier.size(60.dp))
        }
        Text(text = "Cart", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.padding(8.dp))
    }
}
@Composable
fun CheckoutDetailView(checkoutDetails: CheckoutDetails){
    Column(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.fillMaxWidth()) {
            CheckoutItemView("SubTotal",checkoutDetails.subTotal,"USD")
            CheckoutItemView("Tax",checkoutDetails.tax,"USD")
            CheckoutItemView("Delivery Fee",checkoutDetails.deliveryFee,"USD")
            CheckoutItemView("Total",checkoutDetails.totalAmount,"USD")
        }
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(Orange)) {
            Text(text = "CHECKOUT", fontSize = 16.sp, modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp), fontFamily = poppinsFontFamily)
        }
    }
}

@Composable
fun CheckoutItemView(title:String,value:Double,currency:String){
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)){
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = StringUtils.formatCurrency(value), style = MaterialTheme.typography.titleMedium)
            Text(text = currency, style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
        }
        VerticalDivider()
    }
}
@Composable
fun CartItemView(item: CartItem,onIncrement:()->Unit,onDecrement:()->Unit,onRemove:(CartItem)->Unit){
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp).clip(RoundedCornerShape(16.dp))) {
        AsyncImage(
            model = item.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.menuItemId.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick={ onRemove.invoke(item)}) {
                    Image(painter = painterResource(R.drawable.clear), contentDescription = null, modifier = Modifier.size(24.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.menuItemId.description, fontSize = 12.sp, color = Color.Gray)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$ "+item.menuItemId.price, fontSize = 16.sp, color = Orange)
                Spacer(modifier = Modifier.weight(1f))
                ItemCounter(onCounterIncrement = {
                        onIncrement.invoke()
                    },
                    onCounterDecrement = {
                        onDecrement.invoke()
                    },
                    count = item.quantity)
            }
        }
    }
}



