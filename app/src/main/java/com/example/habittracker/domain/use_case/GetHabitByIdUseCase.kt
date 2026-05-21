package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(id: Long): Habit? {
        return repository.getHabitById(id)
    }
}
