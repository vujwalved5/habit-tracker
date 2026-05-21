package com.example.habittracker.presentation.add_habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.use_case.GetHabitByIdUseCase
import com.example.habittracker.domain.use_case.SaveHabitUseCase
import com.example.habittracker.presentation.reminder.HabitReminderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val saveHabitUseCase: SaveHabitUseCase,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val reminderManager: HabitReminderManager
) : ViewModel() {

    var habitId by mutableStateOf<Long?>(null)
        private set

    var habitName by mutableStateOf("")
        private set

    var icon by mutableStateOf("✨")
        private set

    var frequency by mutableStateOf("Daily")
        private set

    var reminderTime by mutableStateOf<String?>(null)
        private set

    fun loadHabit(id: Long) {
        viewModelScope.launch {
            getHabitByIdUseCase(id)?.let { habit ->
                habitId = habit.id
                habitName = habit.name
                icon = habit.icon
                frequency = habit.frequency
                reminderTime = habit.reminderTime
            }
        }
    }

    fun updateHabitName(name: String) {
        habitName = name
    }

    fun updateIcon(newIcon: String) {
        icon = newIcon
    }

    fun updateFrequency(newFrequency: String) {
        frequency = newFrequency
    }

    fun updateReminderTime(time: String?) {
        reminderTime = time
    }

    fun saveHabit(onSaved: () -> Unit) {
        if (habitName.isBlank()) return
        
        viewModelScope.launch {
            saveHabitUseCase(
                id = habitId ?: 0,
                name = habitName,
                icon = icon,
                frequency = frequency,
                reminderTime = reminderTime
            )
            reminderTime?.let {
                reminderManager.scheduleReminder(habitName, it)
            }
            onSaved()
        }
    }
}
