package com.example.habittracker.presentation.habit_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.theme.*
import java.time.LocalDate

@Composable
fun Heatmap(
    heatmapData: Map<LocalDate, Int>,
    totalHabits: Int,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    // Show last 35 days (5 weeks) to look more like a grid
    val dates = (34 downTo 0).map { today.minusDays(it.toLong()) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Chunk by 7 to make it look like a real grid if needed, 
        // but the prompt just says "repeating grid" in another context.
        // The image shows a small grid. Let's just update colors and shapes for now.
        
        // Actually, let's make it a FlowRow or similar if we want a grid, 
        // but for now I'll just keep the Row and fix the styling.
        
        dates.take(7 * 4).chunked(7).forEach { week ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { date ->
                    val count = heatmapData[date] ?: 0
                    val ratio = if (totalHabits > 0) count.toFloat() / totalHabits else 0f
                    
                    val color = when {
                        ratio == 0f -> TileDeep
                        ratio < 0.33f -> AmberLow
                        ratio < 0.66f -> AmberMid
                        else -> AmberOchre
                    }

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = color,
                                shape = SharpTiny
                            )
                    )
                }
            }
        }
    }
}
