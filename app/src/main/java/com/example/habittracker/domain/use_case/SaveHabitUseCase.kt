package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class SaveHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(name: String, icon: String, frequency: String, reminderTime: String?, id: Long = 0) {
        val habit = Habit(
            id = id,
            name = name,
            icon = icon,
            frequency = frequency,
            reminderTime = reminderTime
        )
        repository.insertHabit(habit)
    }

    suspend operator fun invoke(habit: Habit) {
        repository.insertHabit(habit)
    }
}
