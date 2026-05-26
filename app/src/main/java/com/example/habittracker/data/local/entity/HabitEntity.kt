package com.example.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String = "✨",
    val frequency: String, // e.g., "DAILY"
    val reminderTime: String?, // e.g., "08:00"
    val duration: Int = 10, // minutes
    val category: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
