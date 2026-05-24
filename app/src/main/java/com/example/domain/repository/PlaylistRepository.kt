package com.example.domain.repository

import com.example.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(id: Long)
}
