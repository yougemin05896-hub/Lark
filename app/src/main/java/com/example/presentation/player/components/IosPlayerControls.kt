package com.example.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.iosGlass
import com.example.ui.theme.IosBlue

@Composable
fun IosPlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .iosGlass(darkTheme = true),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPlayPause,
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue)
            ) {
                Text(text = if (isPlaying) "Pause" else "Play", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
