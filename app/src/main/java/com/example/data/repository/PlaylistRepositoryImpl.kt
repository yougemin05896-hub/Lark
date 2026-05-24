package com.example.data.repository

import com.example.data.local.dao.PlaylistDao
import com.example.data.local.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

class PlaylistRepositoryImpl(private val dao: PlaylistDao) {
    suspend fun addPlaylist(name: String) {
        dao.insertPlaylist(PlaylistEntity(name = name))
    }

    fun getPlaylists(): Flow<List<PlaylistEntity>> = dao.getAllPlaylists()
}
