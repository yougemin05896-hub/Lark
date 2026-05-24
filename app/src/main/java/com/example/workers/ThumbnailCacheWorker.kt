package com.example.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ThumbnailCacheWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Extract thumbnails using Coil's VideoFrameDecoder in the background
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
