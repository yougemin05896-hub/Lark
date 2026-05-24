package com.example.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PlaylistRepositoryImpl
import com.example.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(private val repository: PlaylistRepositoryImpl) : ViewModel() {
    
    val playlists = repository.getAllPlaylists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createNewPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }
}
