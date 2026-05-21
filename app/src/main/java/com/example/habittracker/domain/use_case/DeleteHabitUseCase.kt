package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        repository.deleteHabit(habit)
    }
}
