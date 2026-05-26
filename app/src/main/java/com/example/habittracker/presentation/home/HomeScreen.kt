package com.example.habittracker.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.presentation.components.GradientHeader
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import com.example.habittracker.ui.theme.*

@Composable
fun HomeScreen(
    onAddHabit: () -> Unit,
    onHabitClick: (Long) -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    val totalLogs by viewModel.totalCompletions.collectAsState()

    HomeScreenContent(
        habits = habits,
        longestStreak = longestStreak,
        totalLogs = totalLogs,
        onAddHabit = onAddHabit,
        onHabitClick = onHabitClick,
        onToggleHabit = viewModel::onToggleHabit
    )
}

@Composable
fun HomeScreenContent(
    habits: List<Habit>,
    longestStreak: Int,
    totalLogs: Int,
    onAddHabit: () -> Unit,
    onHabitClick: (Long) -> Unit,
    onToggleHabit: (Long) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = Accent,
                contentColor = Color.Black,
                shape = FabShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HomeHeader()
            }

            item {
                StatsGrid(
                    streak = longestStreak,
                    progress = "${habits.count { it.isDoneToday }}/${habits.size}",
                    total = totalLogs
                )
            }

            item {
                TodayChallengeCard(
                    streak = longestStreak
                )
            }

            item {
                Text(
                    text = "Active Habits",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    onToggle = { onToggleHabit(habit.id) },
                    onClick = { onHabitClick(habit.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HomeHeader() {
    GradientHeader(height = 180.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hello, Habit Hero!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        text = "Keep going, you're doing great!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsGrid(streak: Int, progress: String, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), title = "Streak", value = streak.toString())
        StatCard(modifier = Modifier.weight(1f), title = "Week", value = progress)
        StatCard(modifier = Modifier.weight(1f), title = "Total", value = total.toString())
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Primary)
        }
    }
}

@Composable
fun TodayChallengeCard(streak: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Finish Strong: $streak days going",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = Accent,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = if (habit.isDoneToday) LightGreen else Surface
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InputGray),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = habit.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${habit.duration} mins • ${habit.frequency}", style = MaterialTheme.typography.labelSmall)
            }
            
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (habit.isDoneToday) Success else Color.Transparent)
                    .then(if (!habit.isDoneToday) Modifier.border(1.dp, Border, CircleShape) else Modifier)
            ) {
                if (habit.isDoneToday) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WidgetTheme {
        HomeScreenContent(
            habits = listOf(
                Habit(id = 1, name = "Morning Run", icon = "🏃", frequency = "Daily", reminderTime = "07:00", duration = 30, isDoneToday = false),
                Habit(id = 2, name = "Read Book", icon = "📚", frequency = "Daily", reminderTime = "21:00", duration = 20, isDoneToday = true)
            ),
            longestStreak = 5,
            totalLogs = 12,
            onAddHabit = {},
            onHabitClick = {},
            onToggleHabit = {}
        )
    }
}
