package com.example.data.repository

import com.example.data.local.dao.MediaDao
import com.example.data.local.entity.HistoryEntity

class HistoryRepositoryImpl(private val dao: MediaDao) {
    suspend fun saveToHistory(mediaItem: HistoryEntity) {
        dao.insertOrUpdate(mediaItem)
    }

    suspend fun getPlaybackHistory(): List<HistoryEntity> {
        return dao.getHistory()
    }
}
