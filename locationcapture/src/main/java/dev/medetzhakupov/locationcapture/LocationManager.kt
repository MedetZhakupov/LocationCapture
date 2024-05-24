package dev.medetzhakupov.locationcapture

import android.location.Location

interface LocationManager {

    fun startPeriodicUpdates(intervalMillis: Long)
    fun requestSingleUpdate(onSuccess: () -> Unit, onError: (Exception) -> Unit)
    fun stopUpdates()
}
