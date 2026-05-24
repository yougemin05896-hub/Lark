package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PitchBlack = Color(0xFF000000)

private val DarkColorScheme =
  darkColorScheme(
    primary = Color.White, 
    secondary = Color.Gray, 
    tertiary = Color.LightGray,
    background = PitchBlack,
    surface = PitchBlack
  )

@Composable
fun GlassPlayerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  // Force absolute AMOLED Pitch-Black theme for the Glass Player architecture
  val colorScheme = DarkColorScheme

  MaterialTheme(
    colorScheme = colorScheme, 
    typography = Typography, 
    content = content
  )
}
