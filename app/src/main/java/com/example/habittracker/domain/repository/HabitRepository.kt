package com.example.habittracker.domain.repository

import com.example.habittracker.data.local.dao.DayCount
import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabitsWithLogs(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getAllLogs(): Flow<Map<String, List<String>>>
    fun getCompletionCountsForRange(startDate: String): Flow<List<DayCount>>
    fun getWeeklyCompletions(startDate: String, endDate: String): Flow<List<DayCount>>
    suspend fun getTotalLogCount(): Int
    suspend fun insertHabit(habit: Habit)
    suspend fun getHabitById(id: String): Habit?
    suspend fun deleteHabit(habit: Habit)
    suspend fun deleteAllHabits()
    suspend fun toggleHabitDone(habitId: String, date: String)
    suspend fun sync()
    fun getRecentLogsForHabit(habitId: String, sinceDate: String): Flow<List<String>>
    fun getTotalLogCountFlow(): Flow<Int>
    fun getWidgetHabits(): Flow<List<Habit>>
    fun getRecentDatesForHabit(habitId: String, limit: Int): Flow<List<String>>
}
