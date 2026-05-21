package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.DayCount
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepositoryImpl(
    private val dao: HabitDao
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return combine(dao.getAllHabits(), dao.getAllLogs()) { entities, logs ->
            entities.map { entity ->
                Habit(
                    id = entity.id,
                    name = entity.name,
                    icon = entity.icon,
                    frequency = entity.frequency,
                    reminderTime = entity.reminderTime,
                    createdAt = entity.createdAt,
                    completedDates = logs.filter { it.habitId == entity.id }.map { it.date }
                )
            }
        }
    }

    override fun getAllLogs(): Flow<Map<Long, List<String>>> {
        return dao.getAllLogs().map { logs ->
            logs.groupBy { it.habitId }.mapValues { entry ->
                entry.value.map { it.date }
            }
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
                    isDoneToday = habitLogs.any { it.date == today },
                    createdAt = entity.createdAt,
                    completedDates = habitLogs.map { it.date }
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
                reminderTime = habit.reminderTime
            )
        )
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return dao.getHabitById(id)?.let { entity ->
            Habit(
                id = entity.id,
                name = entity.name,
                icon = entity.icon,
                frequency = entity.frequency,
                reminderTime = entity.reminderTime,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(
            HabitEntity(
                id = habit.id,
                name = habit.name,
                icon = habit.icon,
                frequency = habit.frequency,
                reminderTime = habit.reminderTime
            )
        )
    }

    override suspend fun deleteAllHabits() {
        dao.deleteAllHabits()
        dao.deleteAllLogs()
    }

    override suspend fun toggleHabitDone(habitId: Long, date: String) {
        val logs = dao.getLogsByDate(date).first()
        val existingLog = logs.find { it.habitId == habitId }
        if (existingLog != null) {
            dao.deleteLog(habitId, date)
        } else {
            dao.insertLog(HabitLogEntity(habitId = habitId, date = date))
        }
    }
}
