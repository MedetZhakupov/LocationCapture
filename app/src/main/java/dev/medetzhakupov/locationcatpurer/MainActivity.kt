@file:OptIn(ExperimentalPermissionsApi::class)

package dev.medetzhakupov.locationcatpurer

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.coroutineScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import dev.medetzhakupov.locationcapture.LocationManager
import dev.medetzhakupov.locationcapture.LocationManagerBuilder
import dev.medetzhakupov.locationcatpurer.ui.theme.LocationCaptureTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val locationManager: LocationManager by lazy {
        LocationManagerBuilder(this.application)
            .setCoroutineScope(lifecycle.coroutineScope)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LocationCaptureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationPermissionScreen(
                        modifier = Modifier.padding(innerPadding),
                        onStartPeriodicLocationCapture = {
                            locationManager.startPeriodicUpdates(TimeUnit.SECONDS.toMillis(30))
                        },
                        onStopPeriodicLocationCapture = {
                            locationManager.stopUpdates()
                        },
                        onRequestLocationCapture = {
                            locationManager.requestSingleUpdate(
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Location requested!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = {
                                    Toast.makeText(
                                        context,
                                        "Location request failed! ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationPermissionScreen(
    modifier: Modifier = Modifier,
    onStartPeriodicLocationCapture: () -> Unit,
    onStopPeriodicLocationCapture: () -> Unit,
    onRequestLocationCapture: () -> Unit,
) {
    var granted by remember { mutableStateOf(false) }

    val permissionState = permissionState(Manifest.permission.ACCESS_FINE_LOCATION) { wasGranted ->
        if (wasGranted) {
            granted = true
        }
    }


    var showOpenSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showOpenSettingsDialog) {
            Text(text = "Location permission is needed. Please grant it from app settings.")
            return
        }

        if (permissionState.second.status.isGranted) {
            Button(onClick = {
                onStartPeriodicLocationCapture()
            }) {
                Text("Start periodic location capture")
            }
            Button(onClick = {
                onStopPeriodicLocationCapture()
            }) {
                Text("Stop periodic location capture")
            }
            Button(onClick = {
                onRequestLocationCapture()
            }) {
                Text("Manually request location")
            }
            return
        }


        Button(onClick = {
            permissionState.requestPermission(
                onPermissionGranted = {
                    granted = true
                },
                onPermissionDeniedPermanently = {
                    showOpenSettingsDialog = true
                }
            )
        }) {
            Text("Request Location Permission")
        }
    }
}
