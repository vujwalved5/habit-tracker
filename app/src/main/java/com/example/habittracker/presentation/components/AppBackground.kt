package com.example.habittracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.habittracker.presentation.settings.SettingsViewModel
import com.example.habittracker.ui.theme.BackgroundStyle
import com.example.habittracker.ui.theme.CanvasBlack
import com.example.habittracker.ui.theme.TileDeep

@Composable
fun AppBackground(
    viewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val style by viewModel.selectedBackgroundStyle.collectAsState()
    val customUri by viewModel.customBackgroundUri.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(CanvasBlack)) {
        when (style) {
            BackgroundStyle.Pure -> { /* Solid CanvasBlack already set */ }
            BackgroundStyle.GeometricGrid -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridStep = 24.dp.toPx()
                    val gridColor = TileDeep
                    for (x in 0..(size.width / gridStep).toInt()) {
                        drawLine(gridColor, Offset(x * gridStep, 0f), Offset(x * gridStep, size.height), 1.dp.toPx())
                    }
                    for (y in 0..(size.height / gridStep).toInt()) {
                        drawLine(gridColor, Offset(0f, y * gridStep), Offset(size.width, y * gridStep), 1.dp.toPx())
                    }
                }
            }
            BackgroundStyle.DiagonalLines -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 16.dp.toPx()
                    val color = TileDeep
                    val strokeWidth = 1.dp.toPx()
                    // Draw diagonal lines covering the whole screen
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(color, Offset(x, 0f), Offset(x + size.height, size.height), strokeWidth)
                        x += step
                    }
                }
            }
            BackgroundStyle.Topographic -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val color = TileDeep
                    val strokeWidth = 1.dp.toPx()
                    // 3 large concentric rounded rectangles centered off-screen bottom-right
                    val center = Offset(size.width * 1.2f, size.height * 1.2f)
                    drawCircle(color, radius = 400.dp.toPx(), center = center, style = Stroke(strokeWidth))
                    drawCircle(color, radius = 500.dp.toPx(), center = center, style = Stroke(strokeWidth))
                    drawCircle(color, radius = 600.dp.toPx(), center = center, style = Stroke(strokeWidth))
                }
            }
            BackgroundStyle.Custom -> {
                customUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.15f
                    )
                }
            }
        }
        content()
    }
}
