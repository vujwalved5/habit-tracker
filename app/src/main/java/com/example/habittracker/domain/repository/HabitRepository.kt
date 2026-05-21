package com.example.habittracker.domain.repository

import com.example.habittracker.data.local.dao.DayCount
import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabitsWithLogs(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getAllLogs(): Flow<Map<Long, List<String>>>
    fun getCompletionCountsForRange(startDate: String): Flow<List<DayCount>>
    fun getWeeklyCompletions(startDate: String, endDate: String): Flow<List<DayCount>>
    suspend fun getTotalLogCount(): Int
    suspend fun insertHabit(habit: Habit)
    suspend fun getHabitById(id: Long): Habit?
    suspend fun deleteHabit(habit: Habit)
    suspend fun deleteAllHabits()
    suspend fun toggleHabitDone(habitId: Long, date: String)
}
