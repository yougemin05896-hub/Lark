package com.example.domain.usecase

import com.example.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(name: String): Long {
        if (name.isBlank()) throw IllegalArgumentException("Playlist name cannot be empty")
        return repository.createPlaylist(name)
    }
}
