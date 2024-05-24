package dev.medetzhakupov.locationcapture

class MissingLocationPermissionException : Exception(
    "Location Permission is not granted. Make sure to get user's permission first"
)