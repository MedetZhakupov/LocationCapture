package dev.medetzhakupov.locationcapture.data.cache

import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse

internal interface AuthTokenStore {
    fun getAccessToken(): String?
    fun getAccessTokenExpiresAt(): String?
    fun getRefreshToken(): String?
    fun saveAuthTokenResponse(authTokenResponse: AuthTokenResponse)
    fun invalidateCache()
}
