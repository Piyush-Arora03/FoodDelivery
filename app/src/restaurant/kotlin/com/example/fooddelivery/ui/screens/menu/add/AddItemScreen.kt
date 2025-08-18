package com.example.fooddelivery.ui.screens.menu.add

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.navigation.AddMenu
import com.example.fooddelivery.navigation.RestaurantMenuItem
import com.example.fooddelivery.ui.Error
import com.example.fooddelivery.ui.FoodHubTextFiled
import com.example.fooddelivery.ui.HeaderView
import com.example.fooddelivery.ui.Loading
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@Composable
fun AddItemScreen(navController: NavController, viewModel: AddItemViewModel= hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                AddItemViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val name = viewModel.name.collectAsStateWithLifecycle()
    val desc = viewModel.desc.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()
    val imageUri = viewModel.imageUrl.collectAsStateWithLifecycle()

    val isLoading by remember {mutableStateOf(false)}

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null) {
                Toast.makeText(navController.context, "Please Select An Image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.onImageUrlChange(uri)
            }
        }
    )

    val isFormValid = imageUri.value != null && name.value.isNotEmpty() && price.value.isNotEmpty() && desc.value.isNotEmpty()

    Column {
        HeaderView({
            navController.popBackStack()
        },"Add Menu Item")
        when (uiState.value) {
            is UiState.Empty -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ImagePickerSection(imageUri.value,{
                            photoPickerLauncher.launch(PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            ))
                        })
                    }

                    item {
                        FoodHubTextFiled(
                            value = name.value,
                            onValueChange = {viewModel.onNameChange(it)},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Item Name") },
                            singleLine = true
                        )
                    }
                    item {
                        FoodHubTextFiled(
                            value = desc.value,
                            onValueChange = {viewModel.onDescChange(it)},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Item Description") },
                            maxLines = 2
                        )
                    }
                    item {
                        FoodHubTextFiled(
                            value = price.value,
                            onValueChange = {viewModel.onPriceChange(it)},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Item Price") },
                            leadingIcon = {Text("$")},
                            singleLine = true
                        )
                    }
                    item{
                        Spacer(modifier = Modifier.padding(20.dp))
                        Button(
                            onClick = {
                                viewModel.addMenuItem()
                            },
                            modifier = Modifier.fillMaxWidth()
                                .height(50.dp),
                            enabled = isFormValid && !isLoading
                        ) {
                            Log.d("Button","loading-> ${isLoading} and isValid -> ${isFormValid}")
                            if (isLoading) {
                                CircularProgressIndicator(color = Primary)
                            } else {
                                Text("Add Menu Item")
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                Error({
                    viewModel.onTryAgainPressed()
                },"Some Error Occurred","Try Again")
            }
            is UiState.Loading -> {
                Loading()
            }
            is UiState.Success<*> -> {
                val data=(uiState.value as UiState.Success<*>).data as AddItemViewModel.AddItemState
                val msg=data.message
                Toast.makeText(navController.context,msg?.message, Toast.LENGTH_SHORT).show()
                viewModel.restUi()
            }
        }
    }

}


@Composable
private fun ImagePickerSection(imageUri: Uri?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Menu Item Image",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected menu item image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(40.dp),
                        tint = Primary
                    )
                    Text("Select Image", color = Primary)
                }
            }
        }
    }
}