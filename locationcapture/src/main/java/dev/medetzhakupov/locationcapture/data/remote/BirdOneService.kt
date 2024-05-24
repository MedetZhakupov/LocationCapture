package dev.medetzhakupov.locationcapture.data.remote

import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse
import dev.medetzhakupov.locationcapture.data.model.LocationData
import dev.medetzhakupov.locationcapture.data.model.LocationUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface BirdOneService {

    @POST("/auth")
    suspend fun getToken(@Header("Authorization") apiKey: String): AuthTokenResponse

    @POST("/auth/refresh")
    suspend fun refreshToken(@Header("Authorization") refreshToken: String): AuthTokenResponse

    @POST("/location")
    suspend fun updateLocation(
        @Header("Authorization") accessToken: String,
        @Body locationData: LocationData
    ): Response<LocationUpdateResponse>
}
