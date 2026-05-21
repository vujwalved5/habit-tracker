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
import com.example.habittracker.presentation.habit_list.HabitListScreen
import com.example.habittracker.presentation.habits.HabitsScreen
import com.example.habittracker.presentation.profile.ProfileScreen
import com.example.habittracker.presentation.habit_list.StatsContent
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.presentation.components.AppBackground
import com.example.habittracker.ui.theme.*

@Composable
fun HabitNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = CanvasBlack,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(64.dp)
                            .drawBehind {
                                drawLine(
                                    color = BorderSubtle,
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
                                    selectedIconColor = AmberOchre,
                                    selectedTextColor = AmberOchre,
                                    unselectedIconColor = TextDim,
                                    unselectedTextColor = TextDim,
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
                startDestination = Screen.Today,
                modifier = Modifier.padding(padding)
            ) {
                composable<Screen.Today> {
                    HabitListScreen(
                        onAddHabit = {
                            navController.navigate(Screen.AddEditHabit())
                        }
                    )
                }
                composable<Screen.Habits> {
                    HabitsScreen(
                        onAddHabit = { navController.navigate(Screen.AddEditHabit()) },
                        onEditHabit = { id -> navController.navigate(Screen.AddEditHabit(id)) }
                    )
                }
                composable<Screen.Progress> {
                    val viewModel: HabitListViewModel = hiltViewModel()
                    StatsContent(viewModel = viewModel)
                }
                composable<Screen.Profile> {
                    ProfileScreen()
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
