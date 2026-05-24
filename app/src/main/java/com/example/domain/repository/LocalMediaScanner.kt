package com.example.domain.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MediaItem(
    val id: Long,
    val title: String,
    val uri: String,
    val isVideo: Boolean,
    val durationMs: Long = 0L,
    val sizeBytes: Long = 0L
)

class LocalMediaScanner(private val context: Context) {
    suspend fun scanMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE
        )
        
        // Scan Videos
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.query(videoUri, projection, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC")?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(videoUri, id).toString()
                val duration = cursor.getLong(durCol)
                val size = cursor.getLong(sizeCol)
                mediaList.add(MediaItem(id, cursor.getString(nameCol) ?: "Unknown", uri, true, duration, size))
            }
        }
        
        // Scan Audio (Limit to 50 for performance in this MVP)
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.query(audioUri, projection, null, null, null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            var count = 0
            while (cursor.moveToNext() && count < 50) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(audioUri, id).toString()
                val duration = cursor.getLong(durCol)
                val size = cursor.getLong(sizeCol)
                mediaList.add(MediaItem(id, cursor.getString(nameCol) ?: "Unknown Audio", uri, false, duration, size))
                count++
            }
        }
        
        mediaList
    }
}
