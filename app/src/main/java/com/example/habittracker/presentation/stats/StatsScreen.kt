package com.example.habittracker.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.presentation.habit_list.HabitListViewModel
import com.example.habittracker.ui.theme.*

import androidx.compose.ui.tooling.preview.Preview
import com.example.habittracker.ui.theme.WidgetTheme

@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    val totalCompletions by viewModel.totalCompletions.collectAsState()

    StatsScreenContent(
        longestStreak = longestStreak,
        currentStreak = habits.maxOfOrNull { it.currentStreak } ?: 0,
        totalCompletions = totalCompletions,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreenContent(
    longestStreak: Int,
    currentStreak: Int,
    totalCompletions: Int,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) } // Month default
    val tabs = listOf("Week", "Month", "Year")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = InputGray,
                    contentColor = TextSecondary,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedTab == index) Primary else Color.Transparent)
                        ) {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            item {
                Text(text = "Completion Rate", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                CompletionRateChart()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCardLarge(modifier = Modifier.weight(1f), label = "Total Completions", value = totalCompletions.toString())
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCardLarge(modifier = Modifier.weight(1f), label = "Best Streak", value = "$longestStreak days", valueColor = Success)
                    StatCardLarge(modifier = Modifier.weight(1f), label = "Current Streak", value = "$currentStreak days", valueColor = Accent)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    WidgetTheme {
        StatsScreenContent(
            longestStreak = 28,
            currentStreak = 0,
            totalCompletions = 127,
            onBack = {}
        )
    }
}

@Composable
fun CompletionRateChart() {
    // Simple bar chart placeholder
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.3f, 0.8f)
        heights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight(height)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(Primary)
            )
        }
    }
}

@Composable
fun StatCardLarge(modifier: Modifier, label: String, value: String, valueColor: Color = Primary) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineLarge, color = valueColor)
        }
    }
}
