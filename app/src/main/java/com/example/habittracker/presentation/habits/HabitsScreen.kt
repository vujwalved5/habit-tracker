package com.example.habittracker.presentation.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import com.example.habittracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (String) -> Unit,
    onHabitClick: (String) -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Habits", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
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
        if (habits.isEmpty()) {
            EmptyState(onAddHabit)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitListRow(
                        habit = habit,
                        onClick = { onHabitClick(habit.id) },
                        onEdit = { onEditHabit(habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitListRow(habit: Habit, onClick: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                Text(text = habit.frequency, style = MaterialTheme.typography.labelSmall)
            }
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyState(onAddHabit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎯", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No habits yet", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Create your first habit to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddHabit,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Add Habit")
        }
    }
}
