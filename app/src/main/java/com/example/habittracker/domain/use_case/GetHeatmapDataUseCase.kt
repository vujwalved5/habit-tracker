package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetHeatmapDataUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Map<LocalDate, Int>> {
        val startDate = LocalDate.now().minusDays(27).toString()
        return repository.getCompletionCountsForRange(startDate).map { list ->
            list.associate { LocalDate.parse(it.day) to it.count }
        }
    }
}
