package com.example.presentation.player.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoJsTimeline(
    currentTimeMs: Long,
    totalTimeMs: Long,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    
    // Sleek razor-thin slider that expands from 2.dp to 6.dp when dragged
    // Utilizes fluid spring physics as per 2026 Liquid Glass specs
    val trackHeight by animateDpAsState(
        targetValue = if (isDragged) 6.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "timelineHeight"
    )

    Slider(
        value = if (totalTimeMs > 0) currentTimeMs.toFloat() / totalTimeMs else 0f,
        onValueChange = onSeek,
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp), // Maintain a reasonable touch target height invisibly
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        ),
        thumb = {
            // Hide the thumb completely when not dragged for the sleek, borderless Video.js look
            if (isDragged) {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = androidx.compose.ui.unit.DpSize(12.dp, 12.dp)
                )
            }
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(trackHeight),
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    )
}
