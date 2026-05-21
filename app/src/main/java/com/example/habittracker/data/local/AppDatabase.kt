package com.example.habittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitLogEntity

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao

    companion object {
        const val DATABASE_NAME = "habit_tracker_db"
    }
}
