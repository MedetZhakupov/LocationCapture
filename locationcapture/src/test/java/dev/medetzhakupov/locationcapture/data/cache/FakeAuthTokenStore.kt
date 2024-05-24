package dev.medetzhakupov.locationcapture.data.cache

import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse

internal class FakeAuthTokenStore: AuthTokenStore {
    private val data = mutableMapOf<String, String?>()

    override fun getAccessToken(): String? {
        return data[ACCESS_TOKEN_KEY]
    }

    override fun getAccessTokenExpiresAt(): String? {
        return data[ACCESS_TOKEN_EXPIRES_AT_KEY]
    }

    override fun getRefreshToken(): String? {
        return data[REFRESH_TOKEN_KEY]
    }

    override fun saveAuthTokenResponse(authTokenResponse: AuthTokenResponse) {
        data[ACCESS_TOKEN_KEY] = authTokenResponse.accessToken
        data[ACCESS_TOKEN_EXPIRES_AT_KEY] = authTokenResponse.expiresAt
        data[REFRESH_TOKEN_KEY] = authTokenResponse.refreshToken
    }

    override fun invalidateCache() {
        data.remove(ACCESS_TOKEN_KEY)
        data.remove(ACCESS_TOKEN_EXPIRES_AT_KEY)
        data.remove(REFRESH_TOKEN_KEY)
    }
}
