package dev.medetzhakupov.locationcapture.data.remote

import android.location.Location
import dev.medetzhakupov.locationcapture.data.model.LocationData

internal class NetworkManager(
    private val accessTokenManager: AccessTokenManager,
    private val birdOneService: BirdOneService,
) {

    suspend fun sendLocationUpdate(location: Location) {
        val accessToken = "Bearer ${accessTokenManager.getAccessToken()}"
        val locationData = LocationData(location.latitude, location.longitude)
        if (birdOneService.updateLocation(accessToken, locationData).code() == 403) {
            val newAccessToken =
                "Bearer ${accessTokenManager.getAccessToken(invalidateAccessToken = true)}"
            birdOneService.updateLocation(newAccessToken, locationData)
        }
    }
}
