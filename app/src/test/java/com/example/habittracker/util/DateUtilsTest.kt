package com.example.habittracker.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class DateUtilsTest {

    @Test
    fun `daysSinceCreated calculates correctly`() {
        val today = LocalDate.now()
        val twoDaysAgo = today.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        assertEquals(3, DateUtils.daysSinceCreated(twoDaysAgo))
    }

    @Test
    fun `weeksSinceCreated calculates correctly`() {
        val today = LocalDate.now()
        val twoWeeksAgo = today.minusWeeks(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        assertEquals(3, DateUtils.weeksSinceCreated(twoWeeksAgo))
    }

    @Test
    fun `weekdaysSinceCreated calculates correctly`() {
        // Find a Monday
        val monday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        val mondayMillis = monday.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        
        val today = LocalDate.now()
        var expected = 0
        var current = monday
        while(current.isBefore(today) || current == today) {
            if (current.dayOfWeek.value <= 5) expected++
            current = current.plusDays(1)
        }
        
        assertEquals(expected, DateUtils.weekdaysSinceCreated(mondayMillis))
    }
}
