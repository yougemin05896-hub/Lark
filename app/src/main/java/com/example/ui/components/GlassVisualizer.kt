package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.IosPink
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Simulates an iOS style audio visualizer.
 */
@Composable
fun GlassVisualizer(modifier: Modifier = Modifier) {
    var amplitudes by remember { mutableStateOf(List(20) { 0f }) }

    LaunchedEffect(Unit) {
        while (true) {
            amplitudes = List(20) { Random.nextFloat() }
            delay(100)
        }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val barWidth = size.width / (amplitudes.size * 2)
        amplitudes.forEachIndexed { index, amplitude ->
            val barHeight = size.height * amplitude
            drawLine(
                color = IosPink.copy(alpha = 0.8f),
                start = Offset(x = index * barWidth * 2f + barWidth, y = size.height),
                end = Offset(x = index * barWidth * 2f + barWidth, y = size.height - barHeight),
                strokeWidth = barWidth
            )
        }
    }
}
