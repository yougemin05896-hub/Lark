package com.example.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LiquidGlassBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsBottomSheet(
    onDismiss: () -> Unit,
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    val qualities = listOf("Auto", "1080p", "720p", "480p")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        LiquidGlassBox(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                )

                Text(
                    text = "Playback Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Playback Speed Selection
                Text(
                    text = "Playback Speed",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                ) {
                    items(speeds) { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSpeedSelected(speed); onDismiss() }
                                .padding(horizontal = 32.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${speed}x",
                                color = if (currentSpeed == speed) Color.White else Color.Gray,
                                fontWeight = if (currentSpeed == speed) FontWeight.Bold else FontWeight.Normal
                            )
                            if (currentSpeed == speed) {
                                Text(text = "✓", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // Video Quality Selection
                Text(
                    text = "Quality",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                ) {
                    items(qualities) { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Quality selected */ onDismiss() }
                                .padding(horizontal = 32.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = quality,
                                color = if (quality == "Auto") Color.White else Color.Gray,
                                fontWeight = if (quality == "Auto") FontWeight.Bold else FontWeight.Normal
                            )
                            if (quality == "Auto") {
                                Text(text = "✓", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
