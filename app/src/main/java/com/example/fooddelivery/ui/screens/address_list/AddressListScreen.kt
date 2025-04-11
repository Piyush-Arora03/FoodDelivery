package com.example.fooddelivery.ui.screens.address_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.R
import com.example.fooddelivery.navigation.AddAddressScreen
import com.example.fooddelivery.ui.screens.cart.AddressCard
import com.example.fooddelivery.ui.theme.Orange

@Composable
fun AddressList(navController: NavController,viewModel: AddressListViewModel= hiltViewModel()) {
    val state=viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        AddressListScreenHeader(onBackClick = {navController.popBackStack()}, onAddClick = {navController.navigate(AddAddressScreen)})
        when(state.value){
            is AddressListViewModel.AddressListUiState.Success->{
                val data=(state.value as AddressListViewModel.AddressListUiState.Success).data
                LazyColumn {
                    items(data.size){
                        AddressCard(data[it], onAddressClicked = {})
                    }
                }
            }

            is AddressListViewModel.AddressListUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Button(onClick = {
                        viewModel.getAddress()
                    },
                        colors = ButtonDefaults.buttonColors(Orange)) {
                        Text(text = "Retry",style=MaterialTheme.typography.titleLarge)
                    }
                }
            }
            is AddressListViewModel.AddressListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                    Text(text = "Loading..",style=MaterialTheme.typography.titleLarge)
                }
            }
            AddressListViewModel.AddressListUiState.Nothing ->{

            }
        }
    }
}

@Composable
fun AddressListScreenHeader(onBackClick:()->Unit,onAddClick:()->Unit){
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = {onBackClick()}, modifier = Modifier
            .padding(8.dp)
            .align(Alignment.CenterStart)) {
            Image(painter = painterResource(R.drawable.back_button), contentDescription = null, modifier = Modifier.size(60.dp))
        }
        Text(text = "Address", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = {onAddClick.invoke()}, modifier = Modifier.align(Alignment.TopEnd)){
            Image(painter = painterResource(R.drawable.baseline_add_24), contentDescription = null, modifier = Modifier.size(40.dp))
        }
    }
}