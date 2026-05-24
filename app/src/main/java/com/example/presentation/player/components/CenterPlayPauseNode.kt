package com.example.presentation.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.components.LiquidGlassBox
import com.example.ui.theme.GlassIcons
import kotlinx.coroutines.delay

@Composable
fun CenterPlayPauseNode(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // When isPlaying changes, show the icon briefly, then fade it out.
    LaunchedEffect(isPlaying) {
        isVisible = true
        delay(600)
        isVisible = false
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 1.2f)
        ) {
            LiquidGlassBox(
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) GlassIcons.Play else GlassIcons.Pause,
                        contentDescription = "Play/Pause Indicator",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
