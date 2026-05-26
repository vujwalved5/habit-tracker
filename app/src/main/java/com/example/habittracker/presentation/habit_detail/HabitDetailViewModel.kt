package com.example.habittracker.presentation.habit_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.use_case.DeleteHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0L

    val habit: StateFlow<Habit?> = repository.getAllHabitsWithLogs()
        .map { habits -> habits.find { it.id == habitId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun deleteHabit(onDeleted: () -> Unit) {
        viewModelScope.launch {
            habit.value?.let {
                deleteHabitUseCase(it)
                onDeleted()
            }
        }
    }
}
