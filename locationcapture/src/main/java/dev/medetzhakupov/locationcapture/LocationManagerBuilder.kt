package dev.medetzhakupov.locationcapture

import android.content.Context
import com.google.android.gms.location.LocationServices
import dev.medetzhakupov.locationcapture.data.cache.AuthTokenStoreImpl
import dev.medetzhakupov.locationcapture.data.cache.SharedPrefsFactory
import dev.medetzhakupov.locationcapture.data.remote.AccessTokenManager
import dev.medetzhakupov.locationcapture.data.remote.BirdOneService
import dev.medetzhakupov.locationcapture.data.remote.NetworkManager
import dev.medetzhakupov.locationcapture.data.remote.RetrofitFactory
import kotlinx.coroutines.CoroutineScope

class LocationManagerBuilder(private val context: Context) {
    private val baseUrl: String = "https://dummy-api-mobile.api.sandbox.bird.one"
    private var coroutineScope: CoroutineScope? = null

    fun setCoroutineScope(scope: CoroutineScope) = apply { this.coroutineScope = scope }

    fun build(): LocationManager {
        val scope = requireNotNull(coroutineScope) { "CoroutineScope must be provided." }
        val birdOneService = RetrofitFactory(baseUrl).create().create(BirdOneService::class.java)
        val accessTokenManager = AccessTokenManager(
            authTokenStore = AuthTokenStoreImpl(SharedPrefsFactory(context).create()),
            birdOneService = birdOneService,
        )
        val networkManager = NetworkManager(
            accessTokenManager = accessTokenManager,
            birdOneService = birdOneService
        )

        return LocationManagerImpl(
            networkManager,
            LocationServices.getFusedLocationProviderClient(context),
            context,
            scope,
        )
    }
}
