package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern 2026 Liquid Glass math for Jetpack Compose.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    darkTheme: Boolean = isSystemInDarkTheme(),
    blurRadius: Float = 25f,
    alpha: Float = 0.5f // Default alpha for glass color transparency
): Modifier {
    val glassColor = if (darkTheme) {
        Color(0xFF000000).copy(alpha = alpha * 0.5f) // Deep Dark Translucent
    } else {
        Color(0xFFFFFFFF).copy(alpha = alpha * 0.3f) // Light Translucent
    }

    val borderLight = Color.White.copy(alpha = 0.2f)

    return this
        .clip(shape) // Clip before applying background and border
        .graphicsLayer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                renderEffect = android.graphics.RenderEffect.createBlurEffect(
                    blurRadius, blurRadius, android.graphics.Shader.TileMode.DECAL
                ).asComposeRenderEffect()
            }
        }
        .background(glassColor)
        .border(0.5.dp, borderLight, shape)
}
