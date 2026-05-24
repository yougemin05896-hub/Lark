package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.PlayerDatabase
import com.example.domain.repository.LocalMediaScanner
import com.example.data.repository.PlaylistRepositoryImpl
import com.example.data.repository.HistoryRepositoryImpl

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

    val localMediaScanner: LocalMediaScanner by lazy {
        LocalMediaScanner(applicationContext)
    }

    val playlistRepository: PlaylistRepositoryImpl by lazy {
        PlaylistRepositoryImpl(database.playlistDao())
    }

    val historyRepository: HistoryRepositoryImpl by lazy {
        HistoryRepositoryImpl(database.mediaDao())
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
