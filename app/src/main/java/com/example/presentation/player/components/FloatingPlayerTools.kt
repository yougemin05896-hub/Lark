package com.example.presentation.player.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.iosGlass
import com.example.ui.theme.GlassIcons

@Composable
fun FloatingPlayerTools(
    isLocked: Boolean,
    onToggleLock: () -> Unit,
    onToggleSpeed: () -> Unit,
    speedMultiplier: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Lock Button
        IconButton(
            onClick = onToggleLock,
            modifier = Modifier
                .size(48.dp)
                .iosGlass(shape = CircleShape, alpha = 0.6f)
        ) {
            Icon(
                imageVector = if (isLocked) GlassIcons.Lock else GlassIcons.LockOpen,
                contentDescription = "Lock",
                tint = Color.White
            )
        }

        if (!isLocked) {
            // Speed Button
            IconButton(
                onClick = onToggleSpeed,
                modifier = Modifier
                    .size(48.dp)
                    .iosGlass(shape = CircleShape, alpha = 0.6f)
            ) {
                Text(
                    text = "${speedMultiplier}x",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
