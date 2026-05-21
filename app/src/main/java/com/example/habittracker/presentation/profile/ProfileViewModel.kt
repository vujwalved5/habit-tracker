package com.example.habittracker.presentation.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.use_case.GetLongestStreakUseCase
import com.example.habittracker.domain.use_case.GetTotalCompletionsUseCase
import com.example.habittracker.domain.use_case.GetHabitsUseCase
import com.example.habittracker.domain.use_case.ResetAllHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getTotalCompletionsUseCase: GetTotalCompletionsUseCase,
    private val getLongestStreakUseCase: GetLongestStreakUseCase,
    private val getHabitsUseCase: GetHabitsUseCase,
    private val resetAllHabitsUseCase: ResetAllHabitsUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val PROFILE_PICTURE_TYPE_KEY = stringPreferencesKey("profile_picture_type")
    private val PROFILE_PICTURE_VALUE_KEY = stringPreferencesKey("profile_picture_value")

    val userName: StateFlow<String> = dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] ?: "HABIT CHAMPION" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "HABIT CHAMPION")

    val profilePictureType: StateFlow<ProfilePictureType> = dataStore.data
        .map { preferences -> 
            ProfilePictureType.fromString(preferences[PROFILE_PICTURE_TYPE_KEY] ?: "Color")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfilePictureType.Color)

    val profilePictureValue: StateFlow<String> = dataStore.data
        .map { preferences -> preferences[PROFILE_PICTURE_VALUE_KEY] ?: "FFD97706" } // Default AmberOchre
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FFD97706")

    val totalCompletions = getTotalCompletionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longestStreak = getLongestStreakUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalHabits = getHabitsUseCase().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun resetAllHabits() {
        viewModelScope.launch {
            resetAllHabitsUseCase()
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            dataStore.edit { it[USER_NAME_KEY] = name }
        }
    }

    fun updateProfilePicture(type: ProfilePictureType, value: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[PROFILE_PICTURE_TYPE_KEY] = type.type
                it[PROFILE_PICTURE_VALUE_KEY] = value
            }
        }
    }
}
