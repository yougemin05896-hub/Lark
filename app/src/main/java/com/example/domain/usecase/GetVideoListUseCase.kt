package com.example.domain.usecase

import com.example.data.local.entity.VideoEntity
import com.example.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetVideoListUseCase(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<List<VideoEntity>> {
        return repository.getAllVideos()
    }
}
