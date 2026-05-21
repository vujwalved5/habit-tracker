package com.example.habittracker.domain.use_case

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateStreakUseCaseTest {

    private val calculateStreakUseCase = CalculateStreakUseCase()

    @Test
    fun `empty list returns 0`() {
        val result = calculateStreakUseCase(emptyList())
        assertEquals(0, result)
    }

    @Test
    fun `single date returns 1`() {
        val result = calculateStreakUseCase(listOf("2024-05-21"))
        assertEquals(1, result)
    }

    @Test
    fun `consecutive dates returns correct streak`() {
        val dates = listOf("2024-05-21", "2024-05-20", "2024-05-19")
        val result = calculateStreakUseCase(dates)
        assertEquals(3, result)
    }

    @Test
    fun `non-consecutive dates returns longest streak`() {
        val dates = listOf(
            "2024-05-21", "2024-05-20", // streak 2
            "2024-05-18", "2024-05-17", "2024-05-16" // streak 3
        )
        val result = calculateStreakUseCase(dates)
        assertEquals(3, result)
    }

    @Test
    fun `duplicate dates are ignored`() {
        val dates = listOf("2024-05-21", "2024-05-21", "2024-05-20")
        val result = calculateStreakUseCase(dates)
        assertEquals(2, result)
    }

    @Test
    fun `unsorted dates are handled correctly`() {
        val dates = listOf("2024-05-19", "2024-05-21", "2024-05-20")
        val result = calculateStreakUseCase(dates)
        assertEquals(3, result)
    }
}
