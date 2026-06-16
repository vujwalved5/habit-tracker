package com.example.habittracker.domain.model

data class Habit(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val icon: String = "✨",
    val frequency: String,
    val reminderTime: String?,
    val duration: Int = 10,
    val category: String? = null,
    val isDoneToday: Boolean = false,
    val currentStreak: Int = 0,
    val completedDates: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
