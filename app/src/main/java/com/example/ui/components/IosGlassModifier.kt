package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.IosGlassDark
import com.example.ui.theme.IosGlassLight

/**
 * Simulates Apple's frosted glass.
 */
@Composable
fun Modifier.iosGlass(
    blurRadius: Dp = 16.dp,
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier {
    val glassColor = if (darkTheme) IosGlassDark else IosGlassLight
    
    // In Compose, blur applies to the content itself rather than the background behind it
    // unless using advanced RenderNode/RenderEffect setup on a floating window.
    // For general UI, we fake it with a translucent background and simple blur if needed.
    // To properly blur background in standard Jetpack Compose Android, we often need Haze or RenderEffect.
    // We will keep this simple and performant using standard background translucency.
    return this.background(glassColor)
}
