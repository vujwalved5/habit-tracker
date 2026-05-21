package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("DELETE FROM habit_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteLog(habitId: Long, date: String)

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getLogsByDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs")
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    @Query("""
        SELECT strftime('%Y-%m-%d', date) as day, COUNT(*) as count
        FROM habit_logs
        WHERE date >= :startDate
        GROUP BY day
    """)
    fun getCompletionCountsForRange(startDate: String): Flow<List<DayCount>>

    @Query("""
        SELECT strftime('%Y-%m-%d', date) as day, COUNT(*) as count
        FROM habit_logs
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY day
    """)
    fun getWeeklyCompletions(startDate: String, endDate: String): Flow<List<DayCount>>

    @Query("SELECT COUNT(*) FROM habit_logs")
    suspend fun getTotalLogCount(): Int
}

data class DayCount(val day: String, val count: Int)
