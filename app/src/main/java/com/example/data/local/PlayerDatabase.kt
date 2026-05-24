package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.AudioDao
import com.example.data.local.dao.HistoryDao
import com.example.data.local.dao.PlaylistDao
import com.example.data.local.dao.VideoDao
import com.example.data.local.entity.AudioEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.VideoEntity

@Database(
    entities = [VideoEntity::class, AudioEntity::class, PlaylistEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun audioDao(): AudioDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
}
