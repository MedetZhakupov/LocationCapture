package dev.medetzhakupov.locationcapture

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dev.medetzhakupov.locationcapture.data.remote.NetworkManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationManagerImplTest {

    private val networkManager: NetworkManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val fusedLocationClient: FusedLocationProviderClient = mockk(relaxed = true) {
        every { lastLocation } returns mockk(relaxed = true)
    }
    private val scope = TestScope()

    private val locationManager = LocationManagerImpl(
        networkManager,
        fusedLocationClient,
        context,
        scope,
    )

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkStatic(Looper::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(ContextCompat::class)
        unmockkStatic(Looper::class)
    }

    @Test(expected = MissingLocationPermissionException::class)
    fun `startPeriodicUpdates throws MissingLocationPermissionException when permissions are missing`() {
        every { checkSelfPermission(any(), any()) } returns PERMISSION_DENIED
        every { Looper.getMainLooper() } returns mockk()

        locationManager.startPeriodicUpdates(1000)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `startPeriodicUpdates requests location updates when permissions are granted`() =
        scope.runTest {
            every { checkSelfPermission(any(), any()) } returns PERMISSION_GRANTED
            every { Looper.getMainLooper() } returns mockk()
            val locationResult: LocationResult = mockk {
                every { lastLocation } returns mockk(relaxed = true)
            }

            val slotLocationRequest = slot<LocationRequest>()
            val slotLocationCallback = slot<LocationCallback>()
            every {
                fusedLocationClient.requestLocationUpdates(
                    capture(slotLocationRequest),
                    capture(slotLocationCallback),
                    any<Looper>()
                )
            } returns mockk()

            locationManager.startPeriodicUpdates(1000)
            slotLocationCallback.captured.onLocationResult(locationResult)
            advanceUntilIdle()

            assertEquals(1000, slotLocationRequest.captured.intervalMillis)
            coVerify { networkManager.sendLocationUpdate(any()) }
        }

    @Test
    fun `stop updates`() {
        locationManager.stopUpdates()

        verify { fusedLocationClient.removeLocationUpdates(any<LocationCallback>()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `single location request success`() = scope.runTest {
        every { checkSelfPermission(any(), any()) } returns PERMISSION_GRANTED
        val slot = slot<OnSuccessListener<Location>>()
        every {
            fusedLocationClient.lastLocation.addOnSuccessListener(capture(slot))
        } returns fusedLocationClient.lastLocation

        val location: Location = mockk()

        locationManager.requestSingleUpdate(
            onSuccess = {
                assertTrue(true)
            },
            onError = {
                assertTrue(false)
            }
        )
        slot.captured.onSuccess(location)
        advanceUntilIdle()

        coVerify { networkManager.sendLocationUpdate(location) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `single location request failure`() = scope.runTest {
        every { checkSelfPermission(any(), any()) } returns PERMISSION_GRANTED
        val slot = slot<OnFailureListener>()
        every {
            fusedLocationClient.lastLocation.addOnSuccessListener(any())
        } returns fusedLocationClient.lastLocation
        every {
            fusedLocationClient.lastLocation.addOnFailureListener(capture(slot))
        } returns fusedLocationClient.lastLocation
        val error = Exception()

        locationManager.requestSingleUpdate(
            onSuccess = {
                assertTrue(false)
            },
            onError = {
                assertTrue(true)
            }
        )
        slot.captured.onFailure(error)
        advanceUntilIdle()
    }
}