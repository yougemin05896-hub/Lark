package com.example.domain.usecase

import com.example.domain.repository.MediaRepository

class ScanLocalMediaUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() {
        repository.syncLocalMedia()
    }
}
