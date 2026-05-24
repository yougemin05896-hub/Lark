package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_history")
data class MediaItemEntity(
    @PrimaryKey val uri: String,
    val title: String,
    val isVideo: Boolean,
    val lastPlayedPosition: Long,
    val lastPlayedAt: Long = System.currentTimeMillis()
)
