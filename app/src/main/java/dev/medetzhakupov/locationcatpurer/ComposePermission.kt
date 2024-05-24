package dev.medetzhakupov.locationcatpurer

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun permissionState(
    permissionType: String,
    onPermissionRequestResult: (wasGranted: Boolean) -> Unit
): Triple<String, PermissionState, ManagedActivityResultLauncher<String, Boolean>> {
    return Triple(
        permissionType,
        rememberPermissionState(permissionType),
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
            onPermissionRequestResult(wasGranted)
        })
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
fun Triple<String, PermissionState, ManagedActivityResultLauncher<String, Boolean>>.requestPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDeniedPermanently: () -> Unit,
) {
    val (permissionType, permissionState, permissionRequestLauncher) = this
    when (permissionState.status) {
        PermissionStatus.Granted -> {
            onPermissionGranted()
        }

        else -> {
            if (permissionState.status.shouldShowRationale) {
                onPermissionDeniedPermanently()
            } else {
                permissionRequestLauncher.launch(permissionType)
            }
        }
    }
}