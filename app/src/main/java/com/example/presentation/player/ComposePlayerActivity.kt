package com.example.presentation.player

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class ComposePlayerActivity : androidx.appcompat.app.AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 15/16 Modern Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val uriString = intent.getStringExtra("VIDEO_URI") ?: ""
        initializePlayer(uriString)

        setContent {
            com.example.ui.theme.GlassPlayerTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    GlassPlayerUI(player)
                }
            }
        }
    }

    private fun initializePlayer(uriString: String) {
        if(uriString.isEmpty()) {
            Toast.makeText(this, "Empty video URI", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val validUri = Uri.parse(uriString)
            player = ExoPlayer.Builder(this).build().apply {
                setMediaItem(MediaItem.fromUri(validUri))
                prepare()
                playWhenReady = true
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load local media: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

@Composable
fun GlassPlayerUI(player: ExoPlayer?) {
    Box(Modifier.fillMaxSize()) {
        val backdrop = rememberLayerBackdrop()

        // 1. THE MEDIA3 PLAYER SURFACE
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    this.player = player
                }
            },
            modifier = Modifier.layerBackdrop(backdrop)
        )

        // 2. THE DYNAMIC FULL-WIDTH DARK GLASS BOTTOM BAR
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(32.dp) },
                    effects = {
                        vibrancy() // Saturation 1.5
                        blur(20f)
                        lens(16f, 32f)
                    },
                    // DARK GLASS: Combine Black alpha with Palette API vibrant/dominant color
                    onDrawSurface = { drawRect(Color.Black.copy(alpha = 0.4f)) }
                )
                .height(80.dp)
        ) {
            var selectedIndex by remember { mutableIntStateOf(1) }

            // 3. THE SPRING-ANIMATED GLASS INDICATOR
            val indicatorOffset by animateDpAsState(
                targetValue = (selectedIndex * 72).dp + 16.dp, // Calculate exact offset based on icon widths
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "IndicatorAnimation"
            )

            Box(
                Modifier
                    .offset(x = indicatorOffset)
                    .width(64.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    // The indicator itself is a lighter glass layer
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            )

            // 4. THE INTERACTIVE CONTROLS
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedIndex = 0; player?.seekToPrevious() }, modifier = Modifier.width(64.dp)) {
                    Icon(Icons.Filled.FastRewind, contentDescription = "Prev", tint = Color.White)
                }
                IconButton(onClick = { 
                    selectedIndex = 1
                    if (player?.isPlaying == true) player.pause() else player?.play()
                }, modifier = Modifier.width(64.dp)) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play/Pause", tint = Color.White)
                }
                IconButton(onClick = { selectedIndex = 2; player?.seekToNext() }, modifier = Modifier.width(64.dp)) {
                    Icon(Icons.Filled.FastForward, contentDescription = "Next", tint = Color.White)
                }
                IconButton(onClick = { selectedIndex = 3 }, modifier = Modifier.width(64.dp)) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }
    }
}
