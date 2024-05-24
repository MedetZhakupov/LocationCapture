package dev.medetzhakupov.locationcapture.data.remote

import dev.medetzhakupov.locationcapture.TimeUtils
import dev.medetzhakupov.locationcapture.data.cache.AuthTokenStore
import dev.medetzhakupov.locationcapture.data.cache.FakeAuthTokenStore
import dev.medetzhakupov.locationcapture.data.model.AuthTokenResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AccessTokenManagerTest {

    private val authTokenStore: AuthTokenStore = FakeAuthTokenStore()

    private val birdOneService: BirdOneService = mockk()

    private val accessTokenManager = AccessTokenManager(authTokenStore, birdOneService)

    @Test
    fun `test accessToken exists and not expired`() = runTest {
        authTokenStore.saveAuthTokenResponse(
            AuthTokenResponse(
                "existing_token",
                TimeUtils.timeStampToTime(
                    TimeUtils.timeNow().plus(10000)
                ),
                null
            )
        )

        val result = accessTokenManager.getAccessToken()

        assertEquals("existing_token", result)
        coVerify(exactly = 0) { birdOneService.getToken() }
        coVerify(exactly = 0) { birdOneService.refreshToken(any()) }
    }

    @Test
    fun `test accessToken does not exist`() = runTest {
        val mockTokenResponse =
            AuthTokenResponse(
                "new_access_token",
                TimeUtils.timeStampToTime(
                    TimeUtils.timeNow().plus(10000)
                ),
                "refresh_token"
            )
        coEvery { birdOneService.getToken() } returns mockTokenResponse

        val result = accessTokenManager.getAccessToken()

        assertEquals("new_access_token", result)
    }

    @Test
    fun `test accessToken is expired refreshes and save token`() = runTest {
        authTokenStore.saveAuthTokenResponse(
            AuthTokenResponse(
                "existing_token",
                TimeUtils.timeStampToTime(
                    TimeUtils.timeNow().minus(10000)
                ),
                "refresh_token"
            )
        )

        val mockTokenResponse = AuthTokenResponse(
            "refreshed_access_token",
            TimeUtils.timeStampToTime(
                TimeUtils.timeNow().plus(10000)
            ),
            "new_refresh_token"
        )
        coEvery { birdOneService.refreshToken("Bearer refresh_token") } returns mockTokenResponse

        val result = accessTokenManager.getAccessToken()

        assertEquals("refreshed_access_token", result)
    }
}