package com.example.habittracker.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HabitLogDto(
    val id: String,
    val habitId: String,
    val date: String,
    val updatedAt: Long,
    val isDeleted: Boolean
)
