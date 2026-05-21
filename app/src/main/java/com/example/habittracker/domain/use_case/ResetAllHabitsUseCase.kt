package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class ResetAllHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllHabits()
    }
}
