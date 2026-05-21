package com.example.habittracker.domain.use_case

import java.time.LocalDate
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor() {
    operator fun invoke(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.map { LocalDate.parse(it) }.distinct().sortedDescending()
        
        var maxStreak = 0
        var currentStreak = 0
        var expectedDate: LocalDate? = null

        for (date in sortedDates) {
            if (expectedDate == null || date == expectedDate) {
                currentStreak++
                expectedDate = date.minusDays(1)
            } else {
                if (currentStreak > maxStreak) maxStreak = currentStreak
                currentStreak = 1
                expectedDate = date.minusDays(1)
            }
        }
        if (currentStreak > maxStreak) maxStreak = currentStreak

        return maxStreak
    }
}
