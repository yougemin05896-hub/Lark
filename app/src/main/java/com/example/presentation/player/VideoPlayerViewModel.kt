package com.example.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiSubtitleGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoPlayerViewModel : ViewModel() {
    private val aiSubtitleGenerator = AiSubtitleGenerator()

    private val _currentCaption = MutableStateFlow("")
    val currentCaption: StateFlow<String> = _currentCaption

    fun enableAiCaptions(videoUri: String) {
        viewModelScope.launch {
            aiSubtitleGenerator.generateCaptions(videoUri).collect { caption ->
                _currentCaption.value = caption
            }
        }
    }
}
