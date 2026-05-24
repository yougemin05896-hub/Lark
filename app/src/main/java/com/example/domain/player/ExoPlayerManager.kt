package com.example.domain.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalTimeMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isMuted: Boolean = false
)

class ExoPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    fun initializePlayer() {
        if (exoPlayer == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build()

            exoPlayer = ExoPlayer.Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build()
                .apply {
                    addListener(playerListener)
                }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playerState.update {
                it.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    totalTimeMs = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L
                )
            }
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun setMediaItem(uriString: String) {
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playerState.update { it.copy(currentTimeMs = positionMs) }
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun toggleMute() {
        val currentlyMuted = _playerState.value.isMuted
        val newVolume = if (currentlyMuted) 1f else 0f
        exoPlayer?.volume = newVolume
        _playerState.update { it.copy(isMuted = !currentlyMuted) }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    _playerState.update { it.copy(currentTimeMs = player.currentPosition) }
                }
                delay(16) // Smooth 60fps updates
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }

    fun releasePlayer() {
        stopProgressTracker()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
    }
}
