package com.example.habittracker.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habittracker.data.local.dao.DayCount
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity
import com.example.habittracker.data.sync.SyncWorker
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepositoryImpl(
    private val dao: HabitDao,
    private val workManager: WorkManager
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return dao.getAllHabits().map { entities ->
            entities.map { entity ->
                Habit(
                    id = entity.id,
                    name = entity.name,
                    icon = entity.icon,
                    frequency = entity.frequency,
                    reminderTime = entity.reminderTime,
                    duration = entity.duration,
                    category = entity.category,
                    createdAt = entity.createdAt,
                    completedDates = emptyList()
                )
            }
        }
    }

    override fun getAllLogs(): Flow<Map<String, List<String>>> {
        return dao.getAllLogs().map { logs ->
            logs.groupBy { it.habitId }.mapValues { entry ->
                entry.value.map { it.date }
            }
        }
    }

    override fun getRecentLogsForHabit(habitId: String, sinceDate: String): Flow<List<String>> {
        return dao.getRecentLogsForHabit(habitId, sinceDate).map { logs ->
            logs.map { it.date }
        }
    }

    override fun getTopRecentLogsForHabit(habitId: String): Flow<List<String>> {
        return dao.getTopRecentLogsForHabit(habitId).map { logs ->
            logs.map { it.date }
        }
    }

    override fun getCompletionCountsForRange(startDate: String): Flow<List<DayCount>> {
        return dao.getCompletionCountsForRange(startDate)
    }

    override fun getWeeklyCompletions(startDate: String, endDate: String): Flow<List<DayCount>> {
        return dao.getWeeklyCompletions(startDate, endDate)
    }

    override suspend fun getTotalLogCount(): Int {
        return dao.getTotalLogCount()
    }

    override fun getTotalLogCountFlow(): Flow<Int> {
        return dao.getTotalLogCountFlow()
    }

    override fun getAllHabitsWithLogs(): Flow<List<Habit>> {
        val today = LocalDate.now().toString()
        return combine(dao.getAllHabits(), dao.getAllLogs()) { habits, logs ->
            habits.map { entity ->
                val habitLogs = logs.filter { it.habitId == entity.id }
                Habit(
                    id = entity.id,
                    name = entity.name,
                    icon = entity.icon,
                    frequency = entity.frequency,
                    reminderTime = entity.reminderTime,
                    duration = entity.duration,
                    category = entity.category,
                    isDoneToday = habitLogs.any { it.date == today },
                    createdAt = entity.createdAt,
                    completedDates = habitLogs.map { it.date }
                )
            }
        }
    }

    override fun getWidgetHabits(): Flow<List<Habit>> {
        val today = LocalDate.now().toString()
        val sinceDate = LocalDate.now().minusDays(com.example.habittracker.util.Constants.STREAK_WINDOW_DAYS).toString()
        
        return combine(
            dao.getWidgetHabits(today),
            dao.getRecentLogs(sinceDate)
        ) { habits, logs ->
            habits.map { wh ->
                val habitLogs = logs.filter { it.habitId == wh.id }.map { it.date }
                
                // Kotlin streak calc equivalent to CalculateStreakUseCase
                val sortedDates = habitLogs.map { LocalDate.parse(it) }.distinct().sortedDescending()
                var maxStreak = 0
                var currentStreak = 0
                var expectedDate: LocalDate? = null

                for (date in sortedDates) {
                    if (expectedDate == null || date == expectedDate) {
                        currentStreak++
                        expectedDate = date.minusDays(1)
                    } else {
                        if (currentStreak > maxStreak) maxStreak = currentStreak
                        currentStreak = 1
                        expectedDate = date.minusDays(1)
                    }
                }
                if (currentStreak > maxStreak) maxStreak = currentStreak
                
                Habit(
                    id = wh.id,
                    name = wh.name,
                    icon = wh.icon,
                    frequency = "",
                    reminderTime = null,
                    isDoneToday = wh.isDoneToday,
                    currentStreak = maxStreak
                )
            }
        }
    }

    override suspend fun insertHabit(habit: Habit) {
        dao.insertHabit(
            HabitEntity(
                id = habit.id,
                name = habit.name,
                icon = habit.icon,
                frequency = habit.frequency,
                reminderTime = habit.reminderTime,
                duration = habit.duration,
                category = habit.category,
                createdAt = habit.createdAt,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
        triggerSync()
    }

    override suspend fun getHabitById(id: String): Habit? {
        return dao.getHabitById(id)?.let { entity ->
            Habit(
                id = entity.id,
                name = entity.name,
                icon = entity.icon,
                frequency = entity.frequency,
                reminderTime = entity.reminderTime,
                duration = entity.duration,
                category = entity.category,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.softDeleteHabit(habit.id, System.currentTimeMillis())
        triggerSync()
    }

    override suspend fun deleteAllHabits() {
        val now = System.currentTimeMillis()
        dao.deleteAllHabits(now)
        dao.deleteAllLogs(now)
        triggerSync()
    }

    override suspend fun toggleHabitDone(habitId: String, date: String) {
        val existingLog = dao.getLogsByDate(habitId, date)
        
        if (existingLog != null) {
            if (existingLog.isDeleted) {
                // Re-enable it (Upsert)
                dao.insertLog(existingLog.copy(isDeleted = false, isSynced = false, updatedAt = System.currentTimeMillis()))
            } else {
                // Soft delete it
                dao.softDeleteLog(habitId, date, System.currentTimeMillis())
            }
        } else {
            // Create new
            dao.insertLog(HabitLogEntity(habitId = habitId, date = date, isSynced = false, updatedAt = System.currentTimeMillis()))
        }
        triggerSync()
    }

    override suspend fun sync() {
        triggerSync()
    }

    private fun triggerSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(syncRequest)
    }
}
