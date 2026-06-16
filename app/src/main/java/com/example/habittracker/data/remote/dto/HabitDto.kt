package com.example.habittracker.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HabitDto(
    val id: String,
    val name: String,
    val icon: String,
    val frequency: String,
    val reminderTime: String?,
    val duration: Int,
    val category: String?,
    val createdAt: Long,
    val isDeleted: Boolean
)
