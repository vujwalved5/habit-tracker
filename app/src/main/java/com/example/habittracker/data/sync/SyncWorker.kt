package com.example.habittracker.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.remote.HabitApi
import com.example.habittracker.data.remote.dto.HabitDto
import com.example.habittracker.data.remote.dto.HabitLogDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitDao: HabitDao,
    private val habitApi: HabitApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Push unsynced habits
            val unsyncedHabits = habitDao.getUnsyncedHabits()
            if (unsyncedHabits.isNotEmpty()) {
                val habitDtos = unsyncedHabits.map {
                    HabitDto(
                        id = it.id,
                        name = it.name,
                        icon = it.icon,
                        frequency = it.frequency,
                        reminderTime = it.reminderTime,
                        duration = it.duration,
                        category = it.category,
                        createdAt = it.createdAt,
                        isDeleted = it.isDeleted
                    )
                }
                habitApi.pushHabits(habitDtos)
                habitDao.markHabitsSynced(unsyncedHabits.map { it.id })
            }

            // Push unsynced logs
            val unsyncedLogs = habitDao.getUnsyncedLogs()
            if (unsyncedLogs.isNotEmpty()) {
                val logDtos = unsyncedLogs.map {
                    HabitLogDto(
                        id = it.id,
                        habitId = it.habitId,
                        date = it.date,
                        isDeleted = it.isDeleted
                    )
                }
                habitApi.pushLogs(logDtos)
                habitDao.markLogsSynced(unsyncedLogs.map { it.id })
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
