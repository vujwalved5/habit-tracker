package com.example.habittracker.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity
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
                        updatedAt = it.updatedAt,
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
                        updatedAt = it.updatedAt,
                        isDeleted = it.isDeleted
                    )
                }
                habitApi.pushLogs(logDtos)
                habitDao.markLogsSynced(unsyncedLogs.map { it.id })
            }

            // Pull habits from server
            val remoteHabits = habitApi.fetchAllHabits()
            for (remote in remoteHabits) {
                val local = habitDao.getHabitById(remote.id)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    habitDao.insertHabit(
                        HabitEntity(
                            id = remote.id,
                            name = remote.name,
                            icon = remote.icon,
                            frequency = remote.frequency,
                            reminderTime = remote.reminderTime,
                            duration = remote.duration,
                            category = remote.category,
                            createdAt = remote.createdAt,
                            updatedAt = remote.updatedAt,
                            isSynced = true,
                            isDeleted = remote.isDeleted
                        )
                    )
                }
            }

            // Pull logs from server
            val remoteLogs = habitApi.fetchAllLogs()
            for (remote in remoteLogs) {
                val local = habitDao.getLogById(remote.id)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    habitDao.insertLog(
                        HabitLogEntity(
                            id = remote.id,
                            habitId = remote.habitId,
                            date = remote.date,
                            updatedAt = remote.updatedAt,
                            isSynced = true,
                            isDeleted = remote.isDeleted
                        )
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
