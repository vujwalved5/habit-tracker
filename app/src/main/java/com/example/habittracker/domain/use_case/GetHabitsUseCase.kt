package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {
    operator fun invoke(): Flow<List<Habit>> {
        val today = LocalDate.now().toString()

        return combine(
            repository.getAllHabits(),
            repository.getAllLogs()
        ) { habits, logsMap ->
            habits.map { habit ->
                val habitLogs = logsMap[habit.id] ?: emptyList()
                habit.copy(
                    isDoneToday = habitLogs.contains(today),
                    currentStreak = calculateStreakUseCase(habitLogs),
                    completedDates = habitLogs
                )
            }
        }
    }
}
