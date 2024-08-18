package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

/**
 * Utility class to handle location operations such as requesting location updates and reverse geocoding.
 *
 * @param context The context used to access system services and resources.
 */
class LocationUtils(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Requests location updates and updates the provided ViewModel with the latest location.
     *
     * @param viewModel The ViewModel to update with the new location data.
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    /**
     * Checks if location permissions are granted.
     *
     * @return True if both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are granted, otherwise false.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Converts latitude and longitude into a human-readable address.
     *
     * @param locationData The location data to convert.
     * @return The address as a string or a message indicating that the address was not found.
     */
    fun reverseGeocodeLocation(locationData: LocationData): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(locationData.latitude, locationData.longitude, 1)
        return addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found!"
    }
}
