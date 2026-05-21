package com.example.habittracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DateUtils {
    fun daysSinceCreated(createdAt: Long): Int {
        val createdDate = LocalDate.ofEpochDay(createdAt / (24 * 60 * 60 * 1000))
        return (ChronoUnit.DAYS.between(createdDate, LocalDate.now()) + 1).toInt().coerceAtLeast(0)
    }

    fun weeksSinceCreated(createdAt: Long): Int {
        val createdDate = LocalDate.ofEpochDay(createdAt / (24 * 60 * 60 * 1000))
        return (ChronoUnit.WEEKS.between(createdDate, LocalDate.now()) + 1).toInt().coerceAtLeast(0)
    }

    fun weekdaysSinceCreated(createdAt: Long): Int {
        val createdDate = LocalDate.ofEpochDay(createdAt / (24 * 60 * 60 * 1000))
        var count = 0
        var current = createdDate
        val today = LocalDate.now()
        while (current.isBefore(today) || current == today) {
            if (current.dayOfWeek != DayOfWeek.SATURDAY && current.dayOfWeek != DayOfWeek.SUNDAY) {
                count++
            }
            current = current.plusDays(1)
        }
        return count
    }
}
