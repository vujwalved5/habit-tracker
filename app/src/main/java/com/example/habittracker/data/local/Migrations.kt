package com.example.habittracker.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_logs_habitId_date` ON `habit_logs` (`habitId`, `date`)")
        db.execSQL("DROP INDEX IF EXISTS `index_habit_logs_habitId`")
    }
}
