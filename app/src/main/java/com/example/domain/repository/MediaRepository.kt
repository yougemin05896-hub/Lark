package com.example.domain.repository

import com.example.data.local.entity.AudioEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllVideos(): Flow<List<VideoEntity>>
    fun getAllAudios(): Flow<List<AudioEntity>>
    fun getPlaybackHistory(): Flow<List<HistoryEntity>>
    
    suspend fun syncLocalMedia()
    suspend fun savePlaybackHistory(history: HistoryEntity)
    suspend fun getVideoById(id: String): VideoEntity?
}
