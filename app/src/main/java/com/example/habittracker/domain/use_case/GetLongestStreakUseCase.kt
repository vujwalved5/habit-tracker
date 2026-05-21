package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLongestStreakUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {
    operator fun invoke(): Flow<Int> {
        return repository.getAllLogs().map { logs ->
            if (logs.isEmpty()) return@map 0
            logs.values.maxOfOrNull { calculateStreakUseCase(it) } ?: 0
        }
    }
}
