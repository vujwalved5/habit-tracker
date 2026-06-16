package com.example.habittracker.presentation.add_habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.habittracker.ui.theme.*
import java.util.Calendar
import java.util.Locale

@Composable
fun AddHabitScreen(
    habitId: String? = null,
    onBack: () -> Unit,
    viewModel: AddHabitViewModel = hiltViewModel()
) {
    LaunchedEffect(habitId) {
        habitId?.let { viewModel.loadHabit(it) }
    }

    AddHabitContent(
        habitId = habitId,
        habitName = viewModel.habitName,
        icon = viewModel.icon,
        duration = viewModel.duration,
        frequency = viewModel.frequency,
        reminderTime = viewModel.reminderTime,
        onHabitNameChange = viewModel::updateHabitName,
        onIconChange = viewModel::updateIcon,
        onDurationChange = viewModel::updateDuration,
        onFrequencyChange = viewModel::updateFrequency,
        onReminderTimeChange = viewModel::updateReminderTime,
        onSave = { viewModel.saveHabit(onSaved = onBack) },
        onDelete = { viewModel.deleteHabit(onDeleted = onBack) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitContent(
    habitId: String? = null,
    habitName: String,
    icon: String,
    duration: Int,
    frequency: String,
    reminderTime: String?,
    onHabitNameChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onReminderTimeChange: (String?) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    val frequencies = listOf("Daily", "Weekdays", "Weekends", "Custom")
    val emojiOptions = listOf("🏃", "📚", "💪", "🧘", "😴", "💧", "🍎", "🎸", "✍️", "🌱")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (habitId == null) "Create New Habit" else "Edit Habit", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Habit Name
            FormField(label = "Habit Name") {
                TextField(
                    value = habitName,
                    onValueChange = onHabitNameChange,
                    placeholder = { Text("e.g., Morning Run") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = InputShape,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputGray,
                        unfocusedContainerColor = InputGray,
                        disabledContainerColor = InputGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Icon Selector
            FormField(label = "Choose Icon") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    emojiOptions.take(5).forEach { emoji ->
                        val selected = icon == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(InputShape)
                                .background(if (selected) Primary.copy(alpha = 0.1f) else InputGray)
                                .clickable { onIconChange(emoji) }
                                .then(if (selected) Modifier.border(2.dp, Primary, InputShape) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
            }

            // Duration
            FormField(label = "Duration (minutes)") {
                TextField(
                    value = duration.toString(),
                    onValueChange = { onDurationChange(it.toIntOrNull() ?: 0) },
                    placeholder = { Text("30") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = InputShape,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputGray,
                        unfocusedContainerColor = InputGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Frequency
            FormField(label = "Frequency") {
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(InputShape)
                            .background(InputGray)
                            .clickable { frequencyExpanded = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(frequency)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = frequencyExpanded,
                        onDismissRequest = { frequencyExpanded = false }
                    ) {
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq) },
                                onClick = {
                                    onFrequencyChange(freq)
                                    frequencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Reminder Time
            FormField(label = "Reminder Time") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(InputShape)
                        .background(InputGray)
                        .clickable { showTimePicker = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(reminderTime ?: "HH:MM")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = InputShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Text("Cancel", color = TextSecondary)
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = InputShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = habitName.isNotBlank()
                ) {
                    Text(if (habitId == null) "Create" else "Save")
                }
            }

            if (habitId != null) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Habit", color = Danger)
                }
            }
        }
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    onReminderTimeChange(time)
                    showTimePicker = false
                }) { Text("Confirm", color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun FormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AddHabitPreview() {
    WidgetTheme {
        AddHabitContent(
            habitName = "Morning Run",
            icon = "🏃",
            duration = 30,
            frequency = "Daily",
            reminderTime = "07:00",
            onHabitNameChange = {},
            onIconChange = {},
            onDurationChange = {},
            onFrequencyChange = {},
            onReminderTimeChange = {},
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}
