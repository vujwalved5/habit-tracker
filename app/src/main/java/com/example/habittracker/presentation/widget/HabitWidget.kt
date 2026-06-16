package com.example.habittracker.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun habitRepository(): HabitRepository
}

class HabitWidget : GlanceAppWidget() {

    companion object {
        val habitIdKey = ActionParameters.Key<String>("habitId")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java,
        ).habitRepository()

        provideContent {
            val habits by repository.getAllHabitsWithLogs().collectAsState(initial = emptyList())
            WidgetContent(habits)
        }
    }

    @Composable
    private fun WidgetContent(habits: List<Habit>) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .background(GlanceTheme.colors.surface)
                    .cornerRadius(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Today's Habits",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
                Spacer(modifier = GlanceModifier.height(12.dp))

                if (habits.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No habits for today",
                            style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
                        )
                    }
                } else {
                    val sortedHabits = habits.sortedBy { it.isDoneToday }
                    Column(
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        sortedHabits.take(5).forEach { habit ->
                            HabitItem(habit)
                        }
                        if (habits.size > 5) {
                            Text(
                                text = "+ ${habits.size - 5} more...",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = GlanceTheme.colors.onSurfaceVariant
                                ),
                                modifier = GlanceModifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HabitItem(habit: Habit) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.icon,
                style = TextStyle(fontSize = 18.sp),
                modifier = GlanceModifier.padding(end = 8.dp)
            )
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = habit.name,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (habit.isDoneToday) GlanceTheme.colors.onSurfaceVariant else GlanceTheme.colors.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = "${habit.currentStreak} day streak",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = GlanceTheme.colors.primary
                    )
                )
            }

            val action = actionRunCallback<ToggleHabitAction>(
                actionParametersOf(habitIdKey to habit.id)
            )

            Box(
                modifier = GlanceModifier
                    .size(24.dp)
                    .background(if (habit.isDoneToday) GlanceTheme.colors.primary else GlanceTheme.colors.outline)
                    .clickable(action),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isDoneToday) {
                    Text(
                        text = "✓",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

class ToggleHabitAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val habitId = parameters[HabitWidget.habitIdKey] ?: return
        val repository = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java,
        ).habitRepository()

        repository.toggleHabitDone(habitId, LocalDate.now().toString())
        HabitWidget().update(context, glanceId)
    }
}

class HabitWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HabitWidget()
}
