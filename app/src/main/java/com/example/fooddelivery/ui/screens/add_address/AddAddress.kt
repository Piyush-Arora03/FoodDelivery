package com.example.fooddelivery.ui.screens.add_address

import android.graphics.Camera
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddAddress(navController: NavController,viewModel: AddAddressViewModel= hiltViewModel()) {
    val isPermissionGranted= remember {
        mutableStateOf(false)
    }
    RequestLocationPermission(
        onPermissionGranted = {
            isPermissionGranted.value=true
            viewModel.getLocation()
        },
        onPermissionRejected = {
            Toast.makeText(navController.context,"Permission Denied",Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    )
    if(isPermissionGranted.value==true){
        val location=viewModel.getLocation().collectAsStateWithLifecycle(initialValue = null)
        Column {
            val cameraState= rememberCameraPositionState()
            LaunchedEffect(Unit) {
                location.value?.let {
                    cameraState.position= CameraPosition.fromLatLngZoom(LatLng(it.latitude,it.longitude),13f)
                }
            }
            GoogleMap(
                cameraPositionState = cameraState,
                modifier = Modifier.fillMaxSize(),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                    compassEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true
                )
            ){
                location.value?.let {
                    Marker(
                        state = MarkerState(
                            position = LatLng(it.latitude,it.longitude)
                        )
                    )
                }
            }
        }
    }
    else{
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RequestLocationPermission(onPermissionGranted:()->Unit,onPermissionRejected:()->Unit){
    val context= LocalContext.current
    if(context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)==android.content.pm.PackageManager.PERMISSION_GRANTED &&
        context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)==android.content.pm.PackageManager.PERMISSION_GRANTED){
        onPermissionGranted.invoke()
        return
    }
    val permission= listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()){
        if(it.values.all { it }){
            onPermissionGranted.invoke()
        }
        else{
            onPermissionRejected.invoke()
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission.toTypedArray())
    }
}