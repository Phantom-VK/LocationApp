package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme

/**
 * Main activity to display the location UI and handle location permissions.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel by viewModels()
            LocationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(context = context, viewModel = viewModel, locationUtils = locationUtils)
}

@Composable
fun LocationDisplay(
    context: Context,
    viewModel: LocationViewModel,
    locationUtils: LocationUtils
) {
    val location = viewModel.location.value
    val address = location?.let { locationUtils.reverseGeocodeLocation(it) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Permissions granted, request location updates
                locationUtils.requestLocationUpdates(viewModel)
            }
            else -> {
                // Permissions denied, show appropriate message
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                val message = if (shouldShowRationale) {
                    "Location access is required for this feature to work!"
                } else {
                    "Location access is required for this feature to work! Please enable it in mobile settings."
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (location != null) "Address: ${location.latitude}, ${location.longitude} \n $address" else "Location not available!")

        Button(onClick = {
            if (locationUtils.hasLocationPermission()) {
                // Update location if permissions are granted
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                // Request permissions if not granted
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}
