package dev.medetzhakupov.locationcapture.data.model

import androidx.annotation.Keep

@Keep
internal data class AuthTokenResponse(
    val accessToken: String,
    val expiresAt: String,
    val refreshToken: String?
)