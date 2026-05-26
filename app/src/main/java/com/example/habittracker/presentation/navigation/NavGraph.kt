package com.example.habittracker.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.habittracker.presentation.add_habit.AddHabitScreen
import com.example.habittracker.presentation.onboarding.OnboardingScreen
import com.example.habittracker.presentation.home.HomeScreen
import com.example.habittracker.presentation.habit_detail.HabitDetailScreen
import com.example.habittracker.presentation.habits.HabitsScreen
import com.example.habittracker.presentation.profile.ProfileScreen
import com.example.habittracker.presentation.stats.StatsScreen
import com.example.habittracker.presentation.settings.NotificationSettingsScreen
import com.example.habittracker.presentation.settings.AppearanceScreen
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import com.example.habittracker.presentation.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.presentation.components.AppBackground
import com.example.habittracker.ui.theme.*

@Composable
fun HabitNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val onboardingCompleted by settingsViewModel.onboardingCompleted.collectAsState()

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar && onboardingCompleted) {
                    val outlineColor = MaterialTheme.colorScheme.outline
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(64.dp)
                            .drawBehind {
                                drawLine(
                                    color = outlineColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 0.5.dp.toPx()
                                )
                            }
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                            NavigationBarItem(
                                icon = { 
                                    Icon(
                                        imageVector = item.icon, 
                                        contentDescription = item.title,
                                        modifier = Modifier.size(20.dp)
                                    ) 
                                },
                                label = { 
                                    Text(
                                        text = item.title, 
                                        fontSize = 8.sp,
                                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                                    ) 
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = if (onboardingCompleted) Screen.Today else Screen.Onboarding,
                modifier = Modifier.padding(padding)
            ) {
                composable<Screen.Onboarding> {
                    OnboardingScreen(
                        onGetStarted = {
                            settingsViewModel.completeOnboarding()
                            navController.navigate(Screen.Today) {
                                popUpTo(Screen.Onboarding) { inclusive = true }
                            }
                        }
                    )
                }
                composable<Screen.Today> {
                    HomeScreen(
                        onAddHabit = {
                            navController.navigate(Screen.AddEditHabit())
                        },
                        onHabitClick = { id ->
                            navController.navigate(Screen.HabitDetail(id))
                        }
                    )
                }
                composable<Screen.Habits> {
                    HabitsScreen(
                        onAddHabit = { navController.navigate(Screen.AddEditHabit()) },
                        onEditHabit = { id -> navController.navigate(Screen.AddEditHabit(id)) },
                        onHabitClick = { id -> navController.navigate(Screen.HabitDetail(id)) }
                    )
                }
                composable<Screen.HabitDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<Screen.HabitDetail>()
                    HabitDetailScreen(
                        habitId = route.habitId,
                        onBack = { navController.popBackStack() },
                        onEdit = { id -> navController.navigate(Screen.AddEditHabit(id)) }
                    )
                }
                composable<Screen.Progress> {
                    StatsScreen(onBack = { navController.popBackStack() })
                }
                composable<Screen.Profile> {
                    ProfileScreen(
                        onNotificationsClick = { navController.navigate(Screen.Notifications) },
                        onAppearanceClick = { navController.navigate(Screen.Appearance) }
                    )
                }
                composable<Screen.Notifications> {
                    NotificationSettingsScreen(onBack = { navController.popBackStack() })
                }
                composable<Screen.Appearance> {
                    AppearanceScreen(onBack = { navController.popBackStack() })
                }
                composable<Screen.AddEditHabit> { backStackEntry ->
                    val route = backStackEntry.toRoute<Screen.AddEditHabit>()
                    AddHabitScreen(
                        habitId = route.habitId,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
