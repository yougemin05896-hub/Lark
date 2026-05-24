package com.example.presentation.player

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.presentation.player.components.IosPlayerControls

@Composable
fun VideoPlayerScreen(
    uriString: String,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        viewModel.enableAiCaptions(uriString)
        onDispose {
            exoPlayer.release()
        }
    }

    val currentCaption by viewModel.currentCaption.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false // We draw our custom iOS Glass Controller
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // AI Subtitle Overlay
            if (currentCaption.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp).background(Color(0x88000000)).padding(8.dp)) {
                    Text(text = currentCaption, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            IosPlayerControls(
                isPlaying = isPlaying,
                onPlayPause = {
                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                    isPlaying = !isPlaying
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
