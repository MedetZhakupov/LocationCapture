package dev.medetzhakupov.locationcapture.data.remote

import dev.medetzhakupov.locationcapture.TimeUtils
import dev.medetzhakupov.locationcapture.data.cache.AuthTokenStore


internal class AccessTokenManager(
    private val authTokenStore: AuthTokenStore,
    private val birdOneService: BirdOneService,
) {

    suspend fun getAccessToken(invalidateAccessToken: Boolean = false): String {
        if (invalidateAccessToken) {
            authTokenStore.invalidateCache()
        }

        val accessToken = authTokenStore.getAccessToken()

        if (accessToken == null) {
            authTokenStore.saveAuthTokenResponse(birdOneService.getToken())
            return requireNotNull(authTokenStore.getAccessToken())
        }

        if (isAccessTokenExpired()) {
            authTokenStore.saveAuthTokenResponse(
                birdOneService.refreshToken(
                    "Bearer ${requireNotNull(authTokenStore.getRefreshToken())}"
                )
            )
        }

        return requireNotNull(authTokenStore.getAccessToken())
    }

    private fun isAccessTokenExpired(): Boolean {
        val expiresAt = requireNotNull(authTokenStore.getAccessTokenExpiresAt())
        return TimeUtils.timeToTimeStamp(expiresAt) < TimeUtils.timeNow()
    }
}