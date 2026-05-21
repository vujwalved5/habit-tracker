package com.example.habittracker.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.ui.theme.BackgroundStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val BACKGROUND_STYLE_KEY = stringPreferencesKey("background_style")
    private val BACKGROUND_URI_KEY = stringPreferencesKey("background_uri")

    val selectedBackgroundStyle: StateFlow<BackgroundStyle> = dataStore.data
        .map { preferences ->
            when (preferences[BACKGROUND_STYLE_KEY]) {
                "Grid" -> BackgroundStyle.GeometricGrid
                "Lines" -> BackgroundStyle.DiagonalLines
                "Topo" -> BackgroundStyle.Topographic
                "Custom" -> BackgroundStyle.Custom
                else -> BackgroundStyle.Pure
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BackgroundStyle.Pure)

    val customBackgroundUri: StateFlow<String?> = dataStore.data
        .map { preferences -> preferences[BACKGROUND_URI_KEY] }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setBackgroundStyle(style: BackgroundStyle) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[BACKGROUND_STYLE_KEY] = style.label
            }
        }
    }

    fun setCustomBackgroundUri(uri: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[BACKGROUND_URI_KEY] = uri
                preferences[BACKGROUND_STYLE_KEY] = BackgroundStyle.Custom.label
            }
        }
    }
}
