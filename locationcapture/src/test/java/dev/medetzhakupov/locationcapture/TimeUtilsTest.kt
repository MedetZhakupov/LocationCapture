package dev.medetzhakupov.locationcapture

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun `converts time to timestamp`() {
        val expectedTimeStamp = 1716454177106
        assertEquals(expectedTimeStamp, TimeUtils.timeToTimeStamp("2024-05-23T08:49:37.106Z"))
    }
}