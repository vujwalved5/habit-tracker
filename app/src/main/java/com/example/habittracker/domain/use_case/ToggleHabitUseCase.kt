package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class ToggleHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        val today = LocalDate.now().toString()
        repository.toggleHabitDone(habitId, today)
    }
}
