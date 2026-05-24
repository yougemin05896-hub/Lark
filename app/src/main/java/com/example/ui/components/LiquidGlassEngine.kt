package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Modern 2026 Liquid Glass Engine (Layered Approach).
 * Decouples the blur effect completely from the foreground elements.
 */
@Composable
fun LiquidGlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // 1. BACKGROUND LAYER (Blurred & Translucent)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape) // Clip before drawing to keep edges clean
                .graphicsLayer {
                    // Apply RenderEffect ONLY to this background, protecting foreground text
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            12f, 12f, android.graphics.Shader.TileMode.DECAL
                        ).asComposeRenderEffect()
                    }
                    clip = true
                }
                .background(Color(0x33ffffff)) // A subtle frosty tint
                .border(0.5.dp, Color.White.copy(alpha = 0.2f), shape)
        )
        
        // 2. FOREGROUND LAYER (Crystal Clear, never blurred)
        Box(
            modifier = Modifier.matchParentSize(),
            content = content
        )
    }
}
