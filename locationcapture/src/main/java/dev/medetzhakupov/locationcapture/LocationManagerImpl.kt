package dev.medetzhakupov.locationcapture

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dev.medetzhakupov.locationcapture.data.remote.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class LocationManagerImpl(
    private val networkManager: NetworkManager,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context,
    private val scope: CoroutineScope,
) : LocationManager {

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                scope.launch {
                    handleLocationUpdate(location)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startPeriodicUpdates(intervalMillis: Long) {
        if (!hasLocationPermissions()) {
            throw MissingLocationPermissionException()
        }

        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    override fun requestSingleUpdate(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (!hasLocationPermissions()) {
            throw MissingLocationPermissionException()
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    scope.launch {
                        handleLocationUpdate(it)
                        onSuccess()
                    }
                }
            }
            .addOnFailureListener { exception: Exception -> onError(exception) }
    }

    override fun stopUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private suspend fun handleLocationUpdate(location: Location) {
        try {
            networkManager.sendLocationUpdate(location)
        } catch (exception: Exception) {
            logError("sendLocationUpdate", exception)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
                checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED)
    }

    private fun logError(tag: String, exception: Exception) {
        Log.e("LocationCapture", tag, exception)
    }
}
