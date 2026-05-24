package com.example.domain.usecase

import com.example.data.local.entity.HistoryEntity
import com.example.domain.repository.MediaRepository

class SavePlaybackHistoryUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(mediaId: String, uri: String, position: Long, duration: Long) {
        val history = HistoryEntity(
            mediaId = mediaId,
            uri = uri,
            lastPositionMs = position,
            totalDurationMs = duration,
            lastPlayedAt = System.currentTimeMillis()
        )
        repository.savePlaybackHistory(history)
    }
}
