package dev.medetzhakupov.locationcapture.data.cache

import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthTokenStoreImplTest {

    private val authTokenStore: AuthTokenStore = AuthTokenStoreImpl(FakeSharedPreferences())

    @Test
    fun `save AuthTokenResponse`() {
        val authTokenResponse =
            AuthTokenResponse(
                "token",
                "2024-05-23T08:49:37.106Z",
                "refresh_token"
            )
        authTokenStore.saveAuthTokenResponse(authTokenResponse)
        assertEquals("token", authTokenStore.getAccessToken())
        assertEquals("refresh_token", authTokenStore.getRefreshToken())
        assertEquals("2024-05-23T08:49:37.106Z", authTokenStore.getAccessTokenExpiresAt())
    }

    @Test
    fun `save AuthTokenResponse overwrites existing`() {
        val authTokenResponse =
            AuthTokenResponse(
                "token",
                "2024-05-23T08:49:37.106Z",
                "refresh_token"
            )
        val authTokenResponse2 =
            AuthTokenResponse(
                "token2",
                "2025-05-23T08:49:37.106Z",
                "refresh_token2"
            )
        authTokenStore.saveAuthTokenResponse(authTokenResponse)
        authTokenStore.saveAuthTokenResponse(authTokenResponse2)
        assertEquals("token2", authTokenStore.getAccessToken())
        assertEquals("2025-05-23T08:49:37.106Z", authTokenStore.getAccessTokenExpiresAt())
        assertEquals("refresh_token2", authTokenStore.getRefreshToken())
    }


    @Test
    fun `test AuthTokenResponse not saved returns nulls`() {
        assertNull(authTokenStore.getAccessToken())
        assertNull(authTokenStore.getAccessTokenExpiresAt())
        assertNull(authTokenStore.getRefreshToken())
    }

    @Test
    fun `test invalidate cache`() {
        val authTokenResponse = AuthTokenResponse(
            "token",
            "2024-05-23T08:49:37.106Z",
            "refresh_token"
        )
        authTokenStore.saveAuthTokenResponse(authTokenResponse)

        assertNotNull(authTokenStore.getAccessToken())
        assertNotNull(authTokenStore.getRefreshToken())
        assertNotNull(authTokenStore.getAccessTokenExpiresAt())

        authTokenStore.invalidateCache()

        assertNull(authTokenStore.getAccessToken())
        assertNull(authTokenStore.getRefreshToken())
        assertNull(authTokenStore.getAccessTokenExpiresAt())
    }

}
