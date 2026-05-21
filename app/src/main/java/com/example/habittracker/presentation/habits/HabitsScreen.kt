package com.example.habittracker.presentation.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
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
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import com.example.habittracker.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = CanvasBlack,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Your habits".uppercase(), 
                        fontSize = 9.sp, 
                        color = TextDim, 
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = AmberOchre,
                contentColor = SilverWhite,
                shape = SharpFab,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Habit", modifier = Modifier.size(18.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = habits,
                key = { it.id }
            ) { habit ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteHabit(habit)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Habit deleted",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.addHabit(habit)
                                }
                            }
                            true
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                            DangerRed
                        } else Color.Transparent
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(SharpCard)
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = SilverWhite)
                        }
                    },
                    enableDismissFromStartToEnd = false
                ) {
                    HabitRow(
                        habit = habit,
                        onEdit = { onEditHabit(habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitRow(habit: Habit, onEdit: () -> Unit) {
    Card(
        shape = SharpCard,
        colors = CardDefaults.cardColors(containerColor = TileDeep),
        modifier = Modifier.fillMaxWidth().border(0.5.dp, BorderSubtle, SharpCard)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SurfaceSlate, SharpIcon),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.icon, fontSize = 20.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontWeight = FontWeight.Medium, color = SilverWhite, fontSize = 15.sp)
                Text(habit.frequency, fontSize = 11.sp, color = TextMuted)
            }
            
            Box(
                modifier = Modifier
                    .background(AmberOchre.copy(alpha = 0.15f), SharpTiny)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${habit.currentStreak}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberOchre)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = TextDim, modifier = Modifier.size(20.dp))
            }
        }
    }
}
