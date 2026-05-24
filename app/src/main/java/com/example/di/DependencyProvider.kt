package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.PlayerDatabase
import com.example.data.repository.MediaRepositoryImpl
import com.example.data.repository.PlaylistRepositoryImpl

/**
 * Manual Dependency Injection Container.
 * Avoids Hilt/KSP issues in CI/CD environments.
 */
class DependencyProvider(private val applicationContext: Context) {
    
    val database: PlayerDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            PlayerDatabase::class.java,
            "glassplayer.db"
        ).build()
    }

    val localMediaScanner: com.example.domain.repository.LocalMediaScanner by lazy {
        com.example.domain.repository.LocalMediaScanner(applicationContext)
    }

    val mediaRepository: MediaRepositoryImpl by lazy {
        MediaRepositoryImpl(applicationContext, database.videoDao(), database.audioDao(), database.historyDao())
    }

    val playlistRepository: PlaylistRepositoryImpl by lazy {
        PlaylistRepositoryImpl(database.playlistDao())
    }

    companion object {
        @Volatile
        private var instance: DependencyProvider? = null

        fun getInstance(context: Context): DependencyProvider {
            return instance ?: synchronized(this) {
                instance ?: DependencyProvider(context.applicationContext).also { instance = it }
            }
        }
    }
}
