package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCompletionRateUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Float> {
        return combine(repository.getAllHabits(), repository.getTotalLogCountFlow()) { habits, totalCompleted ->
            if (habits.isEmpty()) return@combine 0f
            
            val totalPossible = habits.sumOf { habit ->
                when (habit.frequency) {
                    "Daily" -> DateUtils.daysSinceCreated(habit.createdAt)
                    "Weekly" -> DateUtils.weeksSinceCreated(habit.createdAt)
                    "Weekdays" -> DateUtils.weekdaysSinceCreated(habit.createdAt)
                    else -> DateUtils.daysSinceCreated(habit.createdAt)
                }
            }
            
            if (totalPossible == 0) 0f else (totalCompleted.toFloat() / totalPossible.toFloat()).coerceIn(0f, 1f)
        }
    }
}
