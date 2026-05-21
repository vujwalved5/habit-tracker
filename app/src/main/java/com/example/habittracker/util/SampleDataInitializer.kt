package com.example.habittracker.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataInitializer @Inject constructor(
    private val repository: HabitRepository,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val SEEDED_KEY = booleanPreferencesKey("has_seeded")
    }

    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alreadySeeded = dataStore.data.first()[SEEDED_KEY] ?: false
                if (!alreadySeeded) {
                    val existingHabits = repository.getAllHabits().first()
                    val habitsToSeed = listOf(
                        Habit(name = "Drink Water", icon = "💧", frequency = "Daily", reminderTime = "08:00"),
                        Habit(name = "Exercise", icon = "🏃", frequency = "Daily", reminderTime = "07:00"),
                        Habit(name = "Read", icon = "📖", frequency = "Daily", reminderTime = "21:00")
                    )
                    
                    var seededAny = false
                    habitsToSeed.forEach { habit ->
                        if (existingHabits.none { it.name == habit.name }) {
                            repository.insertHabit(habit)
                            seededAny = true
                        }
                    }
                    
                    if (seededAny || existingHabits.isNotEmpty()) {
                        dataStore.edit { it[SEEDED_KEY] = true }
                    }
                }
            } catch (e: Exception) {
                // Fallback or log error
            }
        }
    }
}
