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
import com.example.presentation.player.components.CenterPlayPauseNode
import com.example.presentation.player.components.VideoJsBottomBar
import com.example.presentation.settings.PlayerSettingsBottomSheet
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    uriString: String,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    
    var currentTime by remember { mutableStateOf(0L) }
    var totalTime by remember { mutableStateOf(0L) }

    var showSettingsSheet by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

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

    LaunchedEffect(isPlaying, showControls) {
        while (showControls) {
            currentTime = exoPlayer.currentPosition
            delay(16) // Smooth 60fps slider updates
        }
    }

    LaunchedEffect(showControls, isPlaying, showSettingsSheet) {
        if (showControls && isPlaying && !showSettingsSheet) {
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
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false 
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Subtitles
            if (currentCaption.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 120.dp else 40.dp)
                        .background(Color(0x88000000))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = currentCaption, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Big Center Play/Pause Indicator (Fades out automatically)
            CenterPlayPauseNode(
                isPlaying = isPlaying,
                modifier = Modifier.align(Alignment.Center)
            )

            // Video.js v10 Sleek Bottom Controls
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                VideoJsBottomBar(
                    isPlaying = isPlaying,
                    currentTimeMs = currentTime,
                    totalTimeMs = totalTime,
                    isMuted = isMuted,
                    onPlayPause = {
                        if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                    },
                    onSeek = { fraction ->
                        val targetPos = (totalTime * fraction).toLong()
                        exoPlayer.seekTo(targetPos)
                        currentTime = targetPos
                    },
                    onToggleMute = {
                        isMuted = !isMuted
                        exoPlayer.volume = if (isMuted) 0f else 1f
                    },
                    onOpenSettings = {
                        showSettingsSheet = true
                    },
                    onToggleFullscreen = {
                        // Normally handle orientation or activity immersive mode here
                    },
                    modifier = Modifier.padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                )
            }

            if (showSettingsSheet) {
                PlayerSettingsBottomSheet(
                    onDismiss = { showSettingsSheet = false },
                    currentSpeed = playbackSpeed,
                    onSpeedSelected = { speed ->
                        playbackSpeed = speed
                        exoPlayer.setPlaybackSpeed(speed)
                    }
                )
            }
        }
    }
}
