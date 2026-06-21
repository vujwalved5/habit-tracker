package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE isDeleted = 0")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id AND isDeleted = 0")
    suspend fun getHabitById(id: String): HabitEntity?

    @Query("UPDATE habits SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteHabit(id: String, updatedAt: Long)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt")
    suspend fun deleteAllHabits(updatedAt: Long)

    @Query("UPDATE habit_logs SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt")
    suspend fun deleteAllLogs(updatedAt: Long)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND isDeleted = 0")
    fun getLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Query("UPDATE habit_logs SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE habitId = :habitId AND date = :date")
    suspend fun softDeleteLog(habitId: String, date: String, updatedAt: Long)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getLogsByDate(habitId: String, date: String): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE id = :id")
    suspend fun getLogById(id: String): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE date = :date AND isDeleted = 0")
    fun getLogsByDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE isDeleted = 0")
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    @Query("""
        SELECT strftime('%Y-%m-%d', date) as day, COUNT(*) as count
        FROM habit_logs
        WHERE date >= :startDate AND isDeleted = 0
        GROUP BY day
    """)
    fun getCompletionCountsForRange(startDate: String): Flow<List<DayCount>>

    @Query("""
        SELECT strftime('%Y-%m-%d', date) as day, COUNT(*) as count
        FROM habit_logs
        WHERE (date BETWEEN :startDate AND :endDate) AND isDeleted = 0
        GROUP BY day
    """)
    fun getWeeklyCompletions(startDate: String, endDate: String): Flow<List<DayCount>>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE isDeleted = 0")
    suspend fun getTotalLogCount(): Int

    @Query("SELECT * FROM habits WHERE isSynced = 0")
    suspend fun getUnsyncedHabits(): List<HabitEntity>

    @Query("SELECT * FROM habit_logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<HabitLogEntity>

    @Query("UPDATE habits SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markHabitsSynced(ids: List<String>)

    @Query("UPDATE habit_logs SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markLogsSynced(ids: List<String>)
}

data class DayCount(val day: String, val count: Int)
