package com.example.habittracker.presentation.add_habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.ui.theme.*
import java.util.Calendar
import java.util.Locale

@Composable
fun AddHabitScreen(
    habitId: Long? = null,
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
        frequency = viewModel.frequency,
        reminderTime = viewModel.reminderTime,
        onHabitNameChange = viewModel::updateHabitName,
        onIconChange = viewModel::updateIcon,
        onFrequencyChange = viewModel::updateFrequency,
        onReminderTimeChange = viewModel::updateReminderTime,
        onSave = { viewModel.saveHabit(onSaved = onBack) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitContent(
    habitId: Long? = null,
    habitName: String,
    icon: String,
    frequency: String,
    reminderTime: String?,
    onHabitNameChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onReminderTimeChange: (String?) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val emojiOptions = listOf("✨", "💧", "🏃", "📖", "🧘", "🥗", "⏰", "💪", "🍎", "🥛", "🚴", "🎸", "✍️", "🌱")
    
    var showTimePicker by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    val frequencies = listOf("Daily", "Weekly", "Weekdays")

    Scaffold(
        containerColor = CanvasBlack,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        (if (habitId == null) "Add Habit" else "Edit Habit").uppercase(), 
                        fontSize = 9.sp, 
                        color = TextDim, 
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SilverWhite)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("HABIT NAME")
                OutlinedTextField(
                    value = habitName,
                    onValueChange = onHabitNameChange,
                    placeholder = { Text("Enter habit name...", color = TextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = SharpCard,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TileDeep,
                        unfocusedContainerColor = TileDeep,
                        focusedBorderColor = AmberOchre,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = SilverWhite,
                        unfocusedTextColor = SilverWhite,
                        cursorColor = AmberOchre
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("FREQUENCY")
                Box {
                    Card(
                        onClick = { frequencyExpanded = true },
                        shape = SharpCard,
                        colors = CardDefaults.cardColors(containerColor = TileDeep),
                        modifier = Modifier.fillMaxWidth().border(0.5.dp, BorderSubtle, SharpCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(frequency, color = SilverWhite)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted)
                        }
                    }
                    DropdownMenu(
                        expanded = frequencyExpanded,
                        onDismissRequest = { frequencyExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f).background(TileDeep).border(0.5.dp, BorderSubtle)
                    ) {
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq, color = SilverWhite) },
                                onClick = {
                                    onFrequencyChange(freq)
                                    frequencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("ICON")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(emojiOptions) { emoji ->
                        val selected = icon == emoji
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(SharpIcon)
                                .background(if (selected) AmberOchre.copy(alpha = 0.2f) else SurfaceSlate)
                                .clickable { onIconChange(emoji) }
                                .border(
                                    width = if (selected) 2.dp else 0.5.dp,
                                    color = if (selected) AmberOchre else BorderSubtle,
                                    shape = SharpIcon
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 24.sp)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel("REMINDER")
                    Switch(
                        checked = reminderTime != null,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showTimePicker = true
                            } else {
                                onReminderTimeChange(null)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SilverWhite,
                            checkedTrackColor = AmberOchre,
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = SurfaceSlate,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
                
                if (reminderTime != null) {
                    Card(
                        onClick = { showTimePicker = true },
                        shape = SharpCard,
                        colors = CardDefaults.cardColors(containerColor = TileDeep),
                        modifier = Modifier.fillMaxWidth().border(0.5.dp, BorderSubtle, SharpCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏰", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(reminderTime ?: "", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = SilverWhite)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = SharpCard,
                colors = ButtonDefaults.buttonColors(containerColor = AmberOchre, contentColor = SilverWhite),
                enabled = habitName.isNotBlank()
            ) {
                Text(if (habitId == null) "CREATE HABIT" else "SAVE CHANGES", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.05.em)
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
            containerColor = TileDeep,
            titleContentColor = SilverWhite,
            textContentColor = TextMuted,
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    onReminderTimeChange(time)
                    showTimePicker = false
                }) { Text("Confirm", color = AmberOchre) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = SilverWhite) }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = SurfaceSlate,
                        clockDialSelectedContentColor = SilverWhite,
                        clockDialUnselectedContentColor = SilverWhite,
                        selectorColor = AmberOchre,
                        periodSelectorBorderColor = BorderSubtle,
                        periodSelectorSelectedContainerColor = AmberOchre.copy(alpha = 0.2f),
                        periodSelectorUnselectedContainerColor = TileDeep,
                        periodSelectorSelectedContentColor = AmberOchre,
                        periodSelectorUnselectedContentColor = SilverWhite,
                        timeSelectorSelectedContainerColor = AmberOchre.copy(alpha = 0.2f),
                        timeSelectorUnselectedContainerColor = TileDeep,
                        timeSelectorSelectedContentColor = AmberOchre,
                        timeSelectorUnselectedContentColor = SilverWhite
                    )
                )
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 9.sp,
        color = TextDim,
        letterSpacing = 0.1.em,
        fontWeight = FontWeight.Normal
    )
}
