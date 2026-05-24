package com.example.presentation.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.domain.player.ExoPlayerManager
import com.example.domain.player.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PlayerUiState(
    val videoTitle: String = "",
    val videoUri: String = ""
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerManager = ExoPlayerManager(application)
    
    val playerState: StateFlow<PlayerState> = playerManager.playerState

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        playerManager.initializePlayer()
    }

    fun loadVideo(uriString: String, title: String = "Unknown Title") {
        _uiState.update { it.copy(videoUri = uriString, videoTitle = title) }
        playerManager.setMediaItem(uriString)
    }

    fun getPlayer() = playerManager.getPlayer()

    fun play() {
        playerManager.play()
    }

    fun pause() {
        playerManager.pause()
    }

    fun togglePlayPause() {
        if (playerState.value.isPlaying) {
            playerManager.pause()
        } else {
            playerManager.play()
        }
    }

    fun seekToFraction(fraction: Float) {
        val total = playerState.value.totalTimeMs
        if (total > 0) {
            playerManager.seekTo((total * fraction).toLong())
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        playerManager.setPlaybackSpeed(speed)
    }

    fun toggleMute() {
        playerManager.toggleMute()
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.releasePlayer()
    }
}
