package dev.medetzhakupov.locationcapture.data.cache

import android.content.SharedPreferences
import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse

internal const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
internal const val ACCESS_TOKEN_EXPIRES_AT_KEY = "ACCESS_TOKEN_EXPIRES_AT_KEY"
internal const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"

internal class AuthTokenStoreImpl(
    private val sharedPrefs: SharedPreferences
) : AuthTokenStore {

    override fun getAccessToken(): String? {
        return sharedPrefs.getString(ACCESS_TOKEN_KEY, null)
    }

    override fun getAccessTokenExpiresAt(): String? {
        return sharedPrefs.getString(ACCESS_TOKEN_EXPIRES_AT_KEY, null)
    }

    override fun getRefreshToken(): String? {
        return sharedPrefs.getString(REFRESH_TOKEN_KEY, null)
    }

    override fun saveAuthTokenResponse(authTokenResponse: AuthTokenResponse) {
        sharedPrefs.edit().putString(ACCESS_TOKEN_KEY, authTokenResponse.accessToken).apply()
        sharedPrefs.edit().putString(ACCESS_TOKEN_EXPIRES_AT_KEY, authTokenResponse.expiresAt).apply()
        authTokenResponse.refreshToken?.let {
            sharedPrefs.edit().putString(REFRESH_TOKEN_KEY, it).apply()
        }
    }

    override fun invalidateCache() {
        sharedPrefs.edit().remove(ACCESS_TOKEN_KEY).apply()
        sharedPrefs.edit().remove(ACCESS_TOKEN_EXPIRES_AT_KEY).apply()
        sharedPrefs.edit().remove(REFRESH_TOKEN_KEY).apply()
    }
}
