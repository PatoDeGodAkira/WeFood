package com.wefood.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF140C29)
val Panel = Color(0xFF24163D)
val PanelLight = Color(0xFF32204F)
val Purple = Color(0xFF8750D6)
val Yellow = Color(0xFFFFC928)
val Cream = Color(0xFFFFF8E8)
val Muted = Color(0xFFB7AACD)
val Success = Color(0xFF62CF96)

private val colors = darkColorScheme(
    primary = Yellow,
    onPrimary = Background,
    secondary = Purple,
    onSecondary = Color.White,
    background = Background,
    onBackground = Color.White,
    surface = Panel,
    onSurface = Color.White,
    surfaceVariant = PanelLight,
    onSurfaceVariant = Muted,
    outline = Color(0xFF594675)
)

@Composable
fun WeFoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}
