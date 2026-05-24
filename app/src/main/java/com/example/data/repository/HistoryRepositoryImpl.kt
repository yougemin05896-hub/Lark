package com.example.data.repository

import com.example.data.local.dao.MediaDao
import com.example.data.local.entities.MediaItemEntity

class HistoryRepositoryImpl(private val dao: MediaDao) {
    suspend fun saveToHistory(mediaItem: MediaItemEntity) {
        dao.insertOrUpdate(mediaItem)
    }

    suspend fun getPlaybackHistory(): List<MediaItemEntity> {
        return dao.getHistory()
    }
}
