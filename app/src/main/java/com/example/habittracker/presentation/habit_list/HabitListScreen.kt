package com.example.habittracker.presentation.habit_list

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittracker.presentation.habit_list.components.Heatmap
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.util.DateUtils
import com.example.habittracker.ui.theme.*
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayContent(
    habits: List<Habit>,
    habitsCompleted: Int,
    totalHabits: Int,
    streak: Int,
    onToggle: (Long) -> Unit,
    onDelete: (Habit) -> Unit,
    onAddHabit: () -> Unit,
    viewModel: HabitListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatsCardsRow(
                completed = habitsCompleted,
                total = totalHabits,
                streak = streak
            )
        }

        item {
            SectionHeader(title = "Today's discipline")
        }

        items(
            items = habits,
            key = { it.id }
        ) { habit ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(habit)
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
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = SilverWhite
                        )
                    }
                },
                enableDismissFromStartToEnd = false
            ) {
                HabitCard(
                    habit = habit,
                    onToggle = { onToggle(habit.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }

        item {
            SectionHeader(title = "Activity grid")
        }

        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                val heatmapData by viewModel.heatmapData.collectAsState()
                Heatmap(
                    heatmapData = heatmapData,
                    totalHabits = totalHabits
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = onAddHabit,
            containerColor = AmberOchre,
            contentColor = SilverWhite,
            shape = SharpFab,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Habit", modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun WeekSummaryContent(viewModel: HabitListViewModel) {
    val weeklyData by viewModel.weeklyData.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val totalHabits = habits.size
    
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
    val endOfWeek = startOfWeek.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column {
                SectionHeader(title = "Week in review")
                Text(
                    text = "${startOfWeek.format(formatter)} - ${endOfWeek.format(formatter)}",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        }

        item {
            val bestDay = weeklyData.maxByOrNull { it.value }?.key
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(weekDays) { index, date ->
                    val count = weeklyData[date] ?: 0
                    val progress = if (totalHabits > 0) count.toFloat() / totalHabits else 0f
                    val isBestDay = bestDay != null && date == bestDay && count > 0
                    DayStatCard(
                        date = date, 
                        progress = progress, 
                        count = count, 
                        totalHabits = totalHabits,
                        isBestDay = isBestDay,
                        backgroundColor = if (index % 2 == 0) TileDeep else TileAlt
                    )
                }
            }
        }
    }
}

@Composable
fun DayStatCard(
    date: LocalDate, 
    progress: Float, 
    count: Int, 
    totalHabits: Int,
    isBestDay: Boolean = false,
    backgroundColor: Color = TileDeep
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val dateNumber = date.dayOfMonth.toString()
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "barAnimation"
    )

    Card(
        shape = SharpCard,
        colors = CardDefaults.cardColors(
            containerColor = if (isBestDay) TileDone else backgroundColor
        ),
        modifier = Modifier
            .width(60.dp)
            .border(0.5.dp, if (isBestDay) BorderDone else BorderSubtle, SharpCard)
            .then(
                if (isBestDay) Modifier.drawBehind {
                    drawLine(
                        color = AmberOchre,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName.uppercase(), fontSize = 9.sp, color = TextDim, letterSpacing = 0.1.em)
            Text(dateNumber, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SilverWhite)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(SurfaceSlate, SharpTiny)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedProgress)
                        .background(AmberOchre, SharpTiny)
                        .align(Alignment.BottomCenter)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("$count/$totalHabits", fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun StatsContent(viewModel: HabitListViewModel) {
    val habits by viewModel.habits.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionHeader(title = "Your numbers")
        }

        item {
            val completionRate by viewModel.completionRate.collectAsState()
            val backgroundColor = TileDeep
            Card(
                shape = SharpCard,
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                modifier = Modifier.fillMaxWidth().border(0.5.dp, BorderSubtle, SharpCard)
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("OVERALL COMPLETION", fontSize = 9.sp, color = TextDim, letterSpacing = 0.1.em)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(contentAlignment = Alignment.Center) {
                            val animatedRate by animateFloatAsState(
                                targetValue = completionRate,
                                animationSpec = tween(1000),
                                label = "donutAnimation"
                            )
                            Canvas(modifier = Modifier.size(140.dp)) {
                                drawArc(
                                    color = SurfaceSlate,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Butt)
                                )
                                drawArc(
                                    color = AmberOchre,
                                    startAngle = -90f,
                                    sweepAngle = animatedRate * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Butt)
                                )
                            }
                            Text(
                                text = "${(animatedRate * 100).toInt()}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = SilverWhite
                            )
                        }
                    }
                    Canvas(modifier = Modifier.size(12.dp).align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)) {
                        drawLine(
                            color = BorderSubtle,
                            start = Offset(size.width, size.height - 12.dp.toPx()),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = BorderSubtle,
                            start = Offset(size.width - 12.dp.toPx(), size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Habit breakdown")
        }

        itemsIndexed(habits) { index, habit ->
            HabitBreakdownRow(habit, backgroundColor = if (index % 2 == 0) TileDeep else TileAlt)
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun HabitBreakdownRow(habit: Habit, backgroundColor: Color = TileDeep) {
    val progress = remember(habit) {
        val expected = when (habit.frequency) {
            "Daily" -> DateUtils.daysSinceCreated(habit.createdAt)
            "Weekly" -> DateUtils.weeksSinceCreated(habit.createdAt)
            "Weekdays" -> DateUtils.weekdaysSinceCreated(habit.createdAt)
            else -> DateUtils.daysSinceCreated(habit.createdAt)
        }.coerceAtLeast(1)
        (habit.completedDates.size.toFloat() / expected).coerceIn(0f, 1f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, SharpCard)
            .border(0.5.dp, BorderSubtle, SharpCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(habit.icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(habit.name, fontWeight = FontWeight.Bold, color = SilverWhite)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(SurfaceSlate)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(AmberOchre)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Text("${habit.currentStreak}", fontWeight = FontWeight.Bold, color = AmberOchre)
                Text("STREAK", fontSize = 8.sp, color = TextDim, letterSpacing = 0.05.em)
            }
            Canvas(modifier = Modifier.size(12.dp).offset(x = 12.dp, y = 12.dp)) {
                drawLine(
                    color = BorderSubtle,
                    start = Offset(size.width, size.height - 12.dp.toPx()),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = BorderSubtle,
                    start = Offset(size.width - 12.dp.toPx(), size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val sortedHabits by remember {
        derivedStateOf {
            habits.sortedWith(
                compareBy<Habit> { it.isDoneToday }
                    .thenByDescending { it.currentStreak }
            )
        }
    }

    val habitsCompleted = remember { derivedStateOf { habits.count { it.isDoneToday } } }
    val totalHabits = remember { derivedStateOf { habits.size } }
    val allCompleted = remember { derivedStateOf { totalHabits.value > 0 && habitsCompleted.value == totalHabits.value } }

    val tabs = listOf("Today", "Week", "Stats")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { HabitTopAppBar() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    val longestStreak by viewModel.longestStreak.collectAsState()
                    val completionRate by viewModel.completionRate.collectAsState()
                    StreakBanner(
                        streak = longestStreak,
                        percentage = (completionRate * 100).toInt()
                    )
                }

                SegmentedTabRow(
                    tabs = tabs,
                    currentPage = pagerState.currentPage,
                    onTabSelected = { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    when (page) {
                        0 -> TodayContent(
                            habits = sortedHabits,
                            habitsCompleted = habitsCompleted.value,
                            totalHabits = totalHabits.value,
                            streak = viewModel.longestStreak.collectAsState().value,
                            onToggle = viewModel::onToggleHabit,
                            onDelete = { habit ->
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
                            },
                            onAddHabit = onAddHabit,
                            viewModel = viewModel
                        )
                        1 -> WeekSummaryContent(viewModel)
                        2 -> StatsContent(viewModel)
                    }
                }
            }

            if (allCompleted.value && pagerState.currentPage == 0) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xD97706, 0xB45309, 0x92400E),
                            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.5, 0.3)
                        )
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTopAppBar() {
    val date = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM")).uppercase() }
    val title = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Build the life you want."
            hour < 17 -> "Keep the momentum going."
            hour < 21 -> "Finish strong today."
            else -> "Rest well. Tomorrow counts."
        }
    }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = date,
                    fontSize = 10.sp,
                    color = TextDim,
                    letterSpacing = 0.05.em,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = SilverWhite,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 24.sp
                )
            }
        },
        actions = {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = TextDim
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AmberOchre, SharpTiny)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .border(1.dp, CanvasBlack, SharpTiny)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun StreakBanner(streak: Int, percentage: Int) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(1000),
        label = "percentageAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SharpCard)
            .background(Brush.linearGradient(listOf(AmberFaint, CanvasBlack)))
            .drawBehind {
                drawLine(
                    color = AmberOchre,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .border(0.5.dp, BorderSubtle, SharpCard)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AmberOchre.copy(alpha = 0.1f), SharpIcon)
                        .border(0.5.dp, AmberOchre.copy(alpha = 0.2f), SharpIcon),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔥", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Current streak",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$streak days going strong",
                        color = SilverWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(54.dp)) {
                    drawArc(
                        color = SurfaceSlate,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Butt)
                    )
                    drawArc(
                        color = AmberOchre,
                        startAngle = -90f,
                        sweepAngle = animatedPercentage * 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Butt)
                    )
                }
                Text(
                    text = "$percentage%",
                    color = SilverWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SegmentedTabRow(
    tabs: List<String>,
    currentPage: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(TileAlt, SharpSmall)
            .border(0.5.dp, BorderSubtle, SharpSmall)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = currentPage == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(if (selected) SharpTiny else SharpSmall)
                    .clickable { onTabSelected(index) }
                    .background(if (selected) AmberOchre else Color.Transparent)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (selected) SilverWhite else TextHint,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun StatsCardsRow(completed: Int, total: Int, streak: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = "$completed/$total",
            label = "Habits done today",
            color = AmberOchre,
            backgroundColor = TileDeep
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = streak.toString(),
            label = "Day streak",
            color = SilverWhite,
            backgroundColor = TileAlt
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, value: String, label: String, color: Color, backgroundColor: Color) {
    Box(
        modifier = modifier
            .height(100.dp)
            .background(backgroundColor, SharpCard)
            .border(0.5.dp, BorderSubtle, SharpCard)
            .padding(16.dp)
    ) {
        Column {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = color)
            Text(label, fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (color == AmberOchre) AmberOchre else TextHint)
            )
        }
        
        Canvas(modifier = Modifier.size(12.dp).align(Alignment.BottomEnd)) {
            drawLine(
                color = BorderSubtle,
                start = Offset(size.width, size.height - 12.dp.toPx()),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = BorderSubtle,
                start = Offset(size.width - 12.dp.toPx(), size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 9.sp,
        color = TextDim,
        letterSpacing = 0.1.em,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun HabitCard(habit: Habit, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    
    val isWarning = remember(habit.reminderTime) {
        habit.reminderTime?.let { timeStr ->
            try {
                val reminder = LocalTime.parse(timeStr)
                val now = LocalTime.now()
                val diff = java.time.Duration.between(now, reminder).toMinutes()
                !habit.isDoneToday && diff in 0..60
            } catch (e: Exception) { false }
        } ?: false
    }

    val cardBg = when {
        habit.isDoneToday -> TileDone
        isWarning -> TileWarn
        else -> TileDeep
    }

    val borderColor = if (habit.isDoneToday) BorderDone else if (isWarning) AmberFaint else BorderSubtle

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(SharpCard)
            .background(cardBg)
            .border(
                width = if (habit.isDoneToday) 0.5.dp else 0.5.dp,
                color = borderColor,
                shape = SharpCard
            )
            .then(
                if (habit.isDoneToday) Modifier.drawBehind {
                    drawLine(
                        color = AmberOchre,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                } else Modifier
            )
    ) {
        if (habit.isDoneToday) {
            Canvas(modifier = Modifier.size(40.dp).align(Alignment.TopEnd)) {
                rotate(45f, pivot = Offset(size.width, 0f)) {
                    drawRect(
                        color = BorderDone,
                        topLeft = Offset(size.width - 20.dp.toPx(), -20.dp.toPx()),
                        size = Size(40.dp.toPx(), 40.dp.toPx()),
                        style = Stroke(width = 0.5.dp.toPx())
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (habit.isDoneToday || isWarning) AmberOchre.copy(alpha = 0.12f) else SurfaceSlate,
                        shape = SharpIcon
                    )
                    .then(
                        if (habit.isDoneToday || isWarning) Modifier.border(0.5.dp, AmberOchre.copy(alpha = 0.25f), SharpIcon) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.icon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontWeight = FontWeight.Medium, color = SilverWhite, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(habit.frequency, fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val badgeBg = if (habit.isDoneToday || isWarning) AmberOchre.copy(alpha = 0.15f) else SurfaceSlate
                    val badgeColor = if (habit.isDoneToday || isWarning) AmberOchre else TextHint
                    
                    Box(
                        modifier = Modifier
                            .background(badgeBg, SharpTiny)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            "${habit.currentStreak} DAYS", 
                            fontSize = 8.sp, 
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            CustomCheckbox(
                checked = habit.isDoneToday,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggle()
                }
            )
        }
    }
}

@Composable
fun CustomCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val scale = remember(checked) { Animatable(1f) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(checked) {
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(100)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .scale(scale.value)
            .background(if (checked) AmberOchre else Color.Transparent, SharpSmall)
            .border(
                width = if (checked) 0.dp else 1.dp,
                color = if (checked) Color.Transparent else BorderSubtle,
                shape = SharpSmall
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = SilverWhite,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
