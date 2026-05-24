package com.example.data.repository

import com.example.data.local.dao.PlaylistDao
import com.example.data.local.entity.PlaylistEntity
import com.example.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                createdAt = System.currentTimeMillis(),
                mediaCount = 0,
                coverImagePath = null
            )
        )
    }

    override suspend fun deletePlaylist(id: Long) {
        playlistDao.deletePlaylist(id)
    }
}
