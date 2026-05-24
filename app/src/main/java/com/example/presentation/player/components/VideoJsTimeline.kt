package com.example.presentation.player.components

import androidx.compose.animation.core.animateDpAsState
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
    
    // Sleek razor-thin slider that expands slightly when dragged
    val trackHeight by animateDpAsState(targetValue = if (isDragged) 6.dp else 2.dp)

    Slider(
        value = if (totalTimeMs > 0) currentTimeMs.toFloat() / totalTimeMs else 0f,
        onValueChange = onSeek,
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight), // Will animate height changes
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        ),
        thumb = {
            // Hide the thumb completely when not dragged for the Video.js sleek look
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
