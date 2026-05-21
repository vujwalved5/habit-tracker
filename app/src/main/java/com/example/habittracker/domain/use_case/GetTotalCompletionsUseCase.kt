package com.example.habittracker.domain.use_case

import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTotalCompletionsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Int> = flow {
        emit(repository.getTotalLogCount())
    }
}
