package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val title: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val thumbnailPath: String?
)
