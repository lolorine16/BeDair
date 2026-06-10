package com.collegedeparis.bedair.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGrey,
    onSecondary = DarkGrey,
    error = ErrorRed,
    background = White,
    surface = White,
    onPrimary = White,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun BeDairTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
