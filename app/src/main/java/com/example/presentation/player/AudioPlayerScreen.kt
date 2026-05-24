package com.example.presentation.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.player.components.IosPlayerControls
import com.example.ui.components.GlassVisualizer
import com.example.ui.theme.IosBlue

@Composable
fun AudioPlayerScreen(uriString: String, viewModel: AudioPlayerViewModel = viewModel()) {
    val isPlaying by viewModel.isPlaying.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "rotateAnim"
    )

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Now Playing", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Rotating Album Art Dummy
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .rotate(if (isPlaying) angle else 0f)
                    .clip(CircleShape)
                    .background(IosBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.DarkGray)
                )
            }

            if (isPlaying) {
                GlassVisualizer(modifier = Modifier.padding(horizontal = 32.dp))
            } else {
                Spacer(modifier = Modifier.height(100.dp))
            }

            IosPlayerControls(
                isPlaying = isPlaying,
                onPlayPause = { viewModel.togglePlay() },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
