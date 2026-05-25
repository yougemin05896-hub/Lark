package com.example.presentation.player

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.skydoves.cloudy.cloudy
import kotlinx.coroutines.delay

class ComposePlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-Edge Experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val uriString = intent.getStringExtra("VIDEO_URI") ?: ""

        // Crash-proof ExoPlayer initialization
        try {
            player = ExoPlayer.Builder(this).build().apply {
                if (uriString.isNotEmpty()) {
                    val uri = Uri.parse(uriString)
                    setMediaItem(MediaItem.fromUri(uri))
                    prepare()
                    playWhenReady = true
                } else {
                    Toast.makeText(this@ComposePlayerActivity, "Empty URI provided.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load video: ${e.message}", Toast.LENGTH_LONG).show()
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black // AMOLED Pitch-Black background
                ) {
                    GlassPlayerUI(
                        player = player,
                        onClose = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

@Composable
fun GlassPlayerUI(player: ExoPlayer?, onClose: () -> Unit) {
    var isControlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(player?.isPlaying ?: false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    
    // Auto-hide controls after 3 seconds of inactivity while playing
    LaunchedEffect(isControlsVisible, isPlaying) {
        if (isControlsVisible && isPlaying) {
            delay(3000)
            isControlsVisible = false
        }
    }

    // Sync ExoPlayer state with Compose UI state
    LaunchedEffect(player) {
        while (true) {
            if (player != null) {
                currentPosition = player.currentPosition
                duration = player.duration.coerceAtLeast(0L)
                isPlaying = player.isPlaying
            }
            delay(100) // Update faster for smoother timeline progress
        }
    }

    // Main Container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isControlsVisible = !isControlsVisible }
    ) {
        // 1. THE EXOPLAYER SURFACE (Base Layer)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false // We provide our own Glass UI
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. THE GLASS CONTROLS OVERLAY (Top Layer)
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize()) {
                
                // TOP BAR (Close, PiP, Share, Volume)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                     GlassPanel {
                         Row(
                             modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), 
                             horizontalArrangement = Arrangement.spacedBy(16.dp),
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             IconBtn(Icons.Filled.Close, onClick = onClose)
                             IconBtn(Icons.Filled.PictureInPicture)
                             IconBtn(Icons.Filled.Share)
                         }
                     }
                     
                     GlassPanel {
                         Row(
                             modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                             verticalAlignment = Alignment.CenterVertically, 
                             horizontalArrangement = Arrangement.spacedBy(16.dp)
                         ) {
                             Box(
                                 Modifier
                                     .width(50.dp)
                                     .height(4.dp)
                                     .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                             )
                             IconBtn(Icons.Filled.VolumeUp)
                         }
                     }
                }

                // CENTER CONTROLS (Rewind, Play/Pause, Forward)
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     GlassPanel(shape = CircleShape, modifier = Modifier
                         .size(64.dp)
                         .clickable(
                             interactionSource = remember { MutableInteractionSource() },
                             indication = null
                         ) { player?.seekBack() }) {
                         Icon(Icons.Filled.Replay10, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(32.dp))
                     }
                     
                     GlassPanel(shape = CircleShape, modifier = Modifier
                         .size(96.dp)
                         .clickable(
                             interactionSource = remember { MutableInteractionSource() },
                             indication = null
                         ) { 
                             if (isPlaying) player?.pause() else player?.play()
                         }) {
                         Icon(
                             imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                             contentDescription = "Play/Pause", 
                             tint = Color.White, 
                             modifier = Modifier.size(48.dp)
                         )
                     }
                     
                     GlassPanel(shape = CircleShape, modifier = Modifier
                         .size(64.dp)
                         .clickable(
                             interactionSource = remember { MutableInteractionSource() },
                             indication = null
                         ) { player?.seekForward() }) {
                         Icon(Icons.Filled.Forward10, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(32.dp))
                     }
                }
                
                // BOTTOM AREA (Timeline & Settings Pill)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Centered for the new bar
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // CC & Audio Pill (Aligned End)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        GlassPanel {
                             Row(
                                 modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                                 horizontalArrangement = Arrangement.spacedBy(16.dp)
                             ) {
                                 IconBtn(Icons.Filled.Subtitles)
                                 IconBtn(Icons.Outlined.ChatBubbleOutline)
                             }
                        }
                    }

                    // THE NEW DYNAMIC GLASS BOTTOM BAR (Timeline + Controls)
                    InteractiveGlassBottomBar(
                        currentPosition = currentPosition,
                        duration = duration,
                        onSeek = { percent -> player?.seekTo((percent * duration).toLong()) },
                        player = player,
                        isPlaying = isPlaying
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveGlassBottomBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Float) -> Unit,
    player: ExoPlayer?,
    isPlaying: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val barPadding = 48.dp // 24dp on each side
    val totalBarWidth = screenWidth - barPadding
    
    // We have 4 main controls in this new bar: SkipBack, Play/Pause, SkipForward, Settings
    val icons = listOf(
        Icons.Filled.SkipPrevious,
        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
        Icons.Filled.SkipNext,
        Icons.Filled.Settings
    )
    
    var selectedIndex by remember { mutableIntStateOf(1) } // Default to Play/Pause
    
    val itemWidth = totalBarWidth / icons.size

    // Spring-Animated Indicator Offset
    val indicatorOffset by animateDpAsState(
        targetValue = itemWidth * selectedIndex,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "IndicatorAnimation"
    )

    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // TIMELINE SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formatTime = { ms: Long ->
                    val totalSeconds = ms / 1000
                    val minutes = totalSeconds / 60
                    val remainingSeconds = totalSeconds % 60
                    String.format("%02d:%02d", minutes, remainingSeconds)
                }
                
                val remaining = duration - currentPosition
                
                Text(formatTime(currentPosition), color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                Spacer(Modifier.width(16.dp))
                
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onValueChange = onSeek,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
                
                Spacer(Modifier.width(16.dp))
                Text("-${formatTime(remaining.coerceAtLeast(0L))}", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
            
            // CONTROLS SECTION WITH ANIMATED INDICATOR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                // The Sliding Glass Indicator
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(itemWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .width(56.dp)
                            .height(40.dp)
                            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    )
                }

                // The Interactive Buttons
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icons.forEachIndexed { index, icon ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        selectedIndex = index
                                        when (index) {
                                            0 -> player?.seekToPrevious()
                                            1 -> if (isPlaying) player?.pause() else player?.play()
                                            2 -> player?.seekToNext()
                                            3 -> { /* Open Settings Bottom Sheet here */ }
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Control $index",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CORE GLASS COMPONENT (Layered Architecture to prevent blurring the content)
// ---------------------------------------------------------------------------
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // 1. BACKGROUND LAYER (Blurred & Tinted)
        // This isolates the cloudy effect so it ONLY blurs what is behind the Box,
        // and does NOT blur the icons or text placed inside the Box.
        Box(
            modifier = Modifier
                .matchParentSize() // Fills the parent Box exactly
                .clip(shape) // Clips the blur to our desired shape
                .cloudy(radius = 35) // Heavy blur for iOS liquid glass feel
                .background(Color.Black.copy(alpha = 0.45f)) // Dark tint for contrast
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), shape) // Subtle glass edge highlight
        )
        
        // 2. FOREGROUND LAYER (Crystal Clear Content)
        // Everything passed into 'content()' goes here and stays sharp.
        content()
    }
}

@Composable
fun IconBtn(icon: ImageVector, onClick: () -> Unit = {}) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(24.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}
