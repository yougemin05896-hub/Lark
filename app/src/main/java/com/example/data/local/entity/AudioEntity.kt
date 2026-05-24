package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val albumArtPath: String?
)
