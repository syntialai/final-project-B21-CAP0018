package com.bangkit.team18.core.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationManager(
  private val context: Context,
  private val locationCallback: LocationCallback
) {

  companion object {
    private const val INTERVAL = 60L
    private const val FASTEST_INTERVAL = 30L
    private const val MAX_WAIT_TIME = 2L
  }

  private val _receivingLocationUpdates = MutableLiveData(false)
  val receivingLocationUpdates: LiveData<Boolean>
    get() = _receivingLocationUpdates

  private val locationClient by lazy {
    LocationServices.getFusedLocationProviderClient(context)
  }

  private val locationRequest by lazy {
    LocationRequest.create().apply {
      interval = INTERVAL
      fastestInterval = FASTEST_INTERVAL
      maxWaitTime = MAX_WAIT_TIME
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
  }

  @SuppressLint("MissingPermission")
  fun startUpdateLocation() {
    try {
      _receivingLocationUpdates.value = true
      locationClient.requestLocationUpdates(
        locationRequest, locationCallback,
        Looper.getMainLooper()
      )
    } catch (permissionRevoked: SecurityException) {
      _receivingLocationUpdates.value = false
    }
  }

  fun stopUpdateLocation() {
    _receivingLocationUpdates.value = false
    locationClient.removeLocationUpdates(locationCallback)
  }
}