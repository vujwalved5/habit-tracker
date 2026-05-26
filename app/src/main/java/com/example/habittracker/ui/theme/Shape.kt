package com.example.habittracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),    // Inputs, small buttons
    medium = RoundedCornerShape(12.dp), // Cards
    large = RoundedCornerShape(16.dp)   // Large containers
)

val ButtonShape = RoundedCornerShape(100.dp) // Fully rounded for M3 feel or specific size
val InputShape = RoundedCornerShape(8.dp)
val CardShape = RoundedCornerShape(12.dp)
val FabShape = RoundedCornerShape(16.dp) // Specified 8dp in text but image shows rounded
