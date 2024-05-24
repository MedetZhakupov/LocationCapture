package dev.medetzhakupov.locationcapture

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

internal class FusedLocationProviderFactory(private val context: Context) {

    fun create(): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}