package com.example.presentation.player

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.presentation.player.components.FloatingPlayerTools
import com.example.presentation.player.components.PlayerControlsOverlay
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    uriString: String,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    
    var currentTime by remember { mutableStateOf(0L) }
    var totalTime by remember { mutableStateOf(0L) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // Connect state to ExoPlayer
    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                isPlaying = isPlayingState
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    totalTime = exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        exoPlayer.addListener(listener)
        viewModel.enableAiCaptions(uriString)
        
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto update current time
    LaunchedEffect(isPlaying, showControls) {
        while (showControls) {
            currentTime = exoPlayer.currentPosition
            delay(1000)
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying && !isLocked) {
            delay(3500)
            showControls = false
        }
    }

    val currentCaption by viewModel.currentCaption.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        ) {
            // 1. AndroidView for ExoPlayer Surface
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false // Completely custom UI
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // 2. AI Subtitle Overlay
            if (currentCaption.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 140.dp else 40.dp)
                        .background(Color(0x88000000))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = currentCaption, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 3. UI Overlays (Animated)
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    
                    // Floating Tools (Top-ish)
                    FloatingPlayerTools(
                        isLocked = isLocked,
                        onToggleLock = { isLocked = !isLocked },
                        onToggleSpeed = {
                            playbackSpeed = if (playbackSpeed == 1.0f) 1.5f else if (playbackSpeed == 1.5f) 2.0f else 1.0f
                            exoPlayer.setPlaybackSpeed(playbackSpeed)
                        },
                        speedMultiplier = playbackSpeed,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp) // adjusted for insets
                    )
                    
                    // Bottom Glass Controls
                    if (!isLocked) {
                        PlayerControlsOverlay(
                            isPlaying = isPlaying,
                            currentTimeMs = currentTime,
                            totalTimeMs = totalTime,
                            onPlayPause = {
                                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                            },
                            onSeek = { fraction ->
                                val targetPos = (totalTime * fraction).toLong()
                                exoPlayer.seekTo(targetPos)
                                currentTime = targetPos
                            },
                            onNext = { /* Navigate to next item theoretically */ },
                            onPrevious = { exoPlayer.seekTo(0) },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
