package dev.medetzhakupov.locationcapture

import android.util.TimeUtils
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


object TimeUtils {

    fun timeToTimeStamp(time: String): Long {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val offsetDateTime = OffsetDateTime.parse(time, formatter)
        return offsetDateTime.toInstant().toEpochMilli()
    }

    fun timeStampToTime(timeStamp: Long): String {
        val instant = Instant.ofEpochMilli(timeStamp)
        val offsetDateTime = instant.atOffset(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return offsetDateTime.format(formatter)
    }

    fun timeNow() = System.currentTimeMillis()
}