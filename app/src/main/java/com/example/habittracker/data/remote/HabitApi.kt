package com.example.habittracker.data.remote

import com.example.habittracker.data.remote.dto.HabitDto
import com.example.habittracker.data.remote.dto.HabitLogDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HabitApi {

    // TODO: Implement a Pull-Merge strategy (e.g., Last-Write-Wins based on timestamps) for fetchAllHabits
    @GET("/api/habits")
    suspend fun fetchAllHabits(): List<HabitDto>

    @GET("/api/logs")
    suspend fun fetchAllLogs(): List<HabitLogDto>

    // TODO: Backend must process isDeleted=true payloads and delete them in D1
    @POST("/api/sync/habits")
    suspend fun pushHabits(@Body habits: List<HabitDto>)

    // TODO: Backend must process isDeleted=true payloads and delete them in D1
    @POST("/api/sync/logs")
    suspend fun pushLogs(@Body logs: List<HabitLogDto>)
}
