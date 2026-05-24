package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.entities.MediaItemEntity
import com.example.data.local.entities.PlaylistEntity
import com.example.data.local.dao.MediaDao

@Database(entities = [MediaItemEntity::class, PlaylistEntity::class], version = 1, exportSchema = false)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun playlistDao(): com.example.data.local.dao.PlaylistDao
}
