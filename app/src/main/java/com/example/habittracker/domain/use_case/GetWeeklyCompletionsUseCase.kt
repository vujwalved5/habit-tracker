package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetWeeklyCompletionsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Map<LocalDate, Int>> {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = today.with(DayOfWeek.SUNDAY)
        
        return repository.getWeeklyCompletions(monday.toString(), sunday.toString()).map { list ->
            list.associate { LocalDate.parse(it.day) to it.count }
        }
    }
}
