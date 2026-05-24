package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.IosGlassDark
import com.example.ui.theme.IosGlassLight

/**
 * True Apple Frosted Glass math for Jetpack Compose.
 * Blurs the background content (requires drawing the modifier over existing content),
 * applies a translucent color layer, and a subtle reflective white border.
 */
@Composable
fun Modifier.iosGlass(
    blurRadius: Dp = 20.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    darkTheme: Boolean = isSystemInDarkTheme(),
    alpha: Float = 0.5f // Controls intensity of the glass tint
): Modifier {
    val glassColor = if (darkTheme) {
        Color(0xFF2A2A2E).copy(alpha = alpha) // Darker variant for Lark-style dark mode
    } else {
        Color(0xFFF9F9F9).copy(alpha = alpha)
    }

    val borderLight = Color.White.copy(alpha = 0.15f)

    return this
        .clip(shape) // Clip before blurring to ensure sharp edges
        .blur(blurRadius)
        .background(glassColor)
        .border(0.5.dp, borderLight, shape)
}
