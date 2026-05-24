package com.example.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PlaylistRepositoryImpl
import com.example.data.local.entities.PlaylistEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(private val repository: PlaylistRepositoryImpl) : ViewModel() {
    
    val playlists = repository.getPlaylists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createNewPlaylist(name: String) {
        viewModelScope.launch {
            repository.addPlaylist(name)
        }
    }
}
