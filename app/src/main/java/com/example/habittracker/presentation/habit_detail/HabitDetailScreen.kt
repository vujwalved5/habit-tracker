package com.example.habittracker.presentation.habit_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.presentation.add_habit.AddHabitViewModel
import com.example.habittracker.presentation.components.GradientHeader
import com.example.habittracker.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.compose.ui.tooling.preview.Preview
import com.example.habittracker.ui.theme.WidgetTheme

@Composable
fun HabitDetailScreen(
    habitId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val habit by viewModel.habit.collectAsState()

    DetailScreenContent(
        habit = habit,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = { viewModel.deleteHabit(onDeleted = onBack) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    habit: Habit?,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.name ?: "Habit Detail", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        habit?.let { h ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GradientHeader(height = 160.dp) {
                        // Empty header, title is in TopAppBar
                    }
                }

                item {
                    HabitInfoCard(h)
                }

                item {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(h.completedDates.sortedDescending().take(7)) { date ->
                    ActivityItem(date = date, isCompleted = true)
                }
                
                // Show some skipped ones for demo if needed, or just the logs
                
                item {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onEdit(h.id) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = InputShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.1f), contentColor = Primary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Habit")
                        }
                        
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = InputShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Danger),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Habit")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitDetailPreview() {
    WidgetTheme {
        DetailScreenContent(
            habit = Habit(
                id = "1",
                name = "Meditate",
                icon = "🧘",
                duration = 10,
                frequency = "Daily",
                reminderTime = "06:00",
                isDoneToday = true,
                completedDates = listOf("2024-05-26", "2024-05-25")
            ),
            onBack = {},
            onEdit = {},
            onDelete = {}
        )
    }
}

@Composable
fun HabitInfoCard(habit: Habit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-40).dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(InputGray),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = habit.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "${habit.duration} minutes daily", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                if (habit.isDoneToday) {
                    Text(text = "✓ Completed Today", style = MaterialTheme.typography.bodySmall, color = Success)
                }
            }
        }
    }
}

@Composable
fun ActivityItem(date: String, isCompleted: Boolean) {
    val localDate = LocalDate.parse(date)
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd")
    val displayDate = if (localDate == LocalDate.now()) "Today" else if (localDate == LocalDate.now().minusDays(1)) "Yesterday" else localDate.format(formatter)

    val backgroundColor = if (isCompleted) LightGreen else InputGray
    val iconColor = if (isCompleted) Success else TextTertiary
    val icon = if (isCompleted) Icons.Default.Check else Icons.Default.Close

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = displayDate, style = MaterialTheme.typography.titleMedium)
                Text(text = if (isCompleted) "Completed" else "Skipped", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
