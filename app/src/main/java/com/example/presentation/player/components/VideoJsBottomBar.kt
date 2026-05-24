package com.example.presentation.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiquidGlassBox
import com.example.ui.theme.GlassIcons

@Composable
fun VideoJsBottomBar(
    isPlaying: Boolean,
    currentTimeMs: Long,
    totalTimeMs: Long,
    isMuted: Boolean,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            VideoJsTimeline(
                currentTimeMs = currentTimeMs,
                totalTimeMs = totalTimeMs,
                onSeek = onSeek,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Group: Play/Pause and Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (isPlaying) GlassIcons.Pause else GlassIcons.Play,
                            contentDescription = "Play/Pause",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "${formatDuration(currentTimeMs)} / ${formatDuration(totalTimeMs)}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Right Group: Volume, CC, Settings, Fullscreen
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleMute) {
                        Icon(
                            imageVector = if (isMuted) GlassIcons.VolumeOff else GlassIcons.VolumeUp,
                            contentDescription = "Volume",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { /* Captions toggle handled at player level typically */ }) {
                        Icon(
                            imageVector = GlassIcons.Subtitles,
                            contentDescription = "Subtitles",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = GlassIcons.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onToggleFullscreen) {
                        Icon(
                            imageVector = GlassIcons.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0) return "00:00"
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
