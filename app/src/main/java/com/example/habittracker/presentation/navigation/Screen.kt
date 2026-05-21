package com.example.habittracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Today
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object Today : Screen()
    @Serializable
    object Habits : Screen()
    @Serializable
    object Progress : Screen()
    @Serializable
    object Profile : Screen()

    @Serializable
    data class AddEditHabit(val habitId: Long? = null) : Screen()
}

data class BottomNavItem(
    val title: String,
    val route: Any,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Today", Screen.Today, Icons.Outlined.Today),
    BottomNavItem("Habits", Screen.Habits, Icons.AutoMirrored.Outlined.Assignment),
    BottomNavItem("Progress", Screen.Progress, Icons.AutoMirrored.Outlined.ShowChart),
    BottomNavItem("Profile", Screen.Profile, Icons.Outlined.Person)
)
