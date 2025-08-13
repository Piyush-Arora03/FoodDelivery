package com.example.fooddelivery.ui.screens.add_address

import android.graphics.Camera
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fooddelivery.ui.theme.Orange
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
fun AddAddress(navController: NavController, viewModel: AddAddressViewModel = hiltViewModel()) {
    val isPermissionGranted = remember {
        mutableStateOf(false)
    }
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        val navigationEvent=viewModel.navigationEvent.collectLatest {
            when(it){
                AddAddressViewModel.AddAddressEvent.NavigateToAddressList->{
                    Toast.makeText(navController.context,"Address Stored Successfully",Toast.LENGTH_SHORT).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("isAddressAdded",true)
                    navController.popBackStack()
                }

                AddAddressViewModel.AddAddressEvent.ShowFinalDialog -> TODO()
            }
        }
    }
    RequestLocationPermission(
        onPermissionGranted = {
            isPermissionGranted.value = true
            viewModel.getLocation()
        },
        onPermissionRejected = {
            Toast.makeText(navController.context, "Permission Denied", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    )
    if (isPermissionGranted.value == true) {
        val location = viewModel.getLocation().collectAsStateWithLifecycle(initialValue = null)
        val cameraState = rememberCameraPositionState()

        Box {
            location.value?.let {
                LaunchedEffect(Unit) {
                    cameraState.position =
                        CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 13f)
                }
                val centerScreenMarker = remember {
                    mutableStateOf(LatLng(it.latitude, it.longitude))
                }
                LaunchedEffect(key1 = cameraState) {
                    snapshotFlow {
                        cameraState.position.target
                    }.collectLatest {
                        centerScreenMarker.value = it
                        if (!cameraState.isMoving) {
                            viewModel.reverseGeocode(
                                centerScreenMarker.value.latitude,
                                centerScreenMarker.value.longitude
                            )
                        }
                    }
                }
                GoogleMap(
                    cameraPositionState = cameraState,
                    modifier = Modifier.fillMaxSize(),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    )
                ) {
                    centerScreenMarker.value.let {
                        Marker(
                            state = MarkerState(
                                position = LatLng(it.latitude, it.longitude)
                            ),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
                }
            }
            val address = viewModel.address.collectAsStateWithLifecycle(initialValue = null)
            address.value?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(8.dp)
                        .align(Alignment.BottomCenter)
                ) {

                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = it.addressLine1,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "${it.city}, ${it.state}, ${it.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    Button(colors = ButtonDefaults.buttonColors(Orange), onClick = {
                        viewModel.onAddAddressClicked(it)
                    }, modifier = Modifier.align(Alignment.CenterEnd), enabled = if(uiState.value is AddAddressViewModel.AddAddressUiState.AddressStoring) false else true) {
                        if(uiState.value is AddAddressViewModel.AddAddressUiState.AddressStoring) {
                            CircularProgressIndicator(modifier = Modifier.padding(4.dp).size(20.dp))
                        }
                        else{
                            Text(
                                "ADD",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.padding(
                                    vertical = 4.dp, horizontal = 8.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit, onPermissionRejected: () -> Unit) {
    val context = LocalContext.current
    if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
        context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionGranted.invoke()
        return
    }
    val permission = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.all { it }) {
                onPermissionGranted.invoke()
            } else {
                onPermissionRejected.invoke()
            }
        }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission.toTypedArray())
    }
}