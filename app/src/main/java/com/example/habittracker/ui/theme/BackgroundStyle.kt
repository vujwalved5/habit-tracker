package com.example.habittracker.ui.theme

sealed class BackgroundStyle(val label: String) {
    object Pure : BackgroundStyle("Pure Dark")
    object GeometricGrid : BackgroundStyle("Grid")
    object DiagonalLines : BackgroundStyle("Lines")
    object Topographic : BackgroundStyle("Topo")
    object Custom : BackgroundStyle("Custom")
}
