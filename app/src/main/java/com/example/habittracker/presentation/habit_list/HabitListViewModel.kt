package com.example.habittracker.presentation.habit_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val toggleHabitUseCase: ToggleHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val saveHabitUseCase: SaveHabitUseCase,
    private val getTotalCompletionsUseCase: GetTotalCompletionsUseCase,
    private val getLongestStreakUseCase: GetLongestStreakUseCase,
    private val getCompletionRateUseCase: GetCompletionRateUseCase,
    private val getHeatmapDataUseCase: GetHeatmapDataUseCase,
    private val getWeeklyCompletionsUseCase: GetWeeklyCompletionsUseCase
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = getHabitsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalCompletions = getTotalCompletionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longestStreak = getLongestStreakUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completionRate = getCompletionRateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val heatmapData = getHeatmapDataUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val weeklyData = getWeeklyCompletionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun onToggleHabit(habitId: String) {
        viewModelScope.launch {
            toggleHabitUseCase(habitId)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            deleteHabitUseCase(habit)
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            saveHabitUseCase(habit)
        }
    }
}
