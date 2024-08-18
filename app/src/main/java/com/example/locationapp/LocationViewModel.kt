package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel to hold and manage location data.
 */
class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> get() = _location

    /**
     * Updates the current location.
     *
     * @param newLocation The new location data to be updated.
     */
    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }
}
