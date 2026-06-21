package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GetLongestStreakUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {
    operator fun invoke(): Flow<Int> {
        return repository.getAllHabits().flatMapLatest { habits ->
            if (habits.isEmpty()) return@flatMapLatest flowOf(0)
            
            val sinceDate = LocalDate.now().minusDays(Constants.STREAK_WINDOW_DAYS).toString()
            
            val streakFlows = habits.map { habit ->
                repository.getRecentLogsForHabit(habit.id, sinceDate).map { logs ->
                    calculateStreakUseCase(logs)
                }
            }
            
            combine(streakFlows) { streaks ->
                streaks.maxOrNull() ?: 0
            }
        }
    }
}
