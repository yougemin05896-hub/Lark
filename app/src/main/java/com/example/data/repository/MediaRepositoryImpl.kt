package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.data.local.dao.AudioDao
import com.example.data.local.dao.HistoryDao
import com.example.data.local.dao.VideoDao
import com.example.data.local.entity.AudioEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.VideoEntity
import com.example.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MediaRepositoryImpl(
    private val context: Context,
    private val videoDao: VideoDao,
    private val audioDao: AudioDao,
    private val historyDao: HistoryDao
) : MediaRepository {

    override fun getAllVideos(): Flow<List<VideoEntity>> = videoDao.getAllVideos()
    override fun getAllAudios(): Flow<List<AudioEntity>> = audioDao.getAllAudios()
    override fun getPlaybackHistory(): Flow<List<HistoryEntity>> = historyDao.getHistory()

    override suspend fun syncLocalMedia() {
        withContext(Dispatchers.IO) {
            val videos = mutableListOf<VideoEntity>()
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED
            )
            
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id).toString()
                    videos.add(
                        VideoEntity(
                            id = id.toString(),
                            uri = uri,
                            title = cursor.getString(nameCol) ?: "Unknown",
                            duration = cursor.getLong(durCol),
                            size = cursor.getLong(sizeCol),
                            dateAdded = cursor.getLong(dateCol),
                            thumbnailPath = null
                        )
                    )
                }
            }
            videoDao.insertVideos(videos)
        }
    }

    override suspend fun savePlaybackHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    override suspend fun getVideoById(id: String): VideoEntity? {
        return videoDao.getVideoById(id)
    }
}
