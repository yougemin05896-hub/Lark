package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.example.di.DependencyProvider

class GlassPlayerApp : Application(), ImageLoaderFactory {

    lateinit var dependencyProvider: DependencyProvider
        private set

    override fun onCreate() {
        super.onCreate()
        // Initialize global dependencies, analytics, or logging tools here if needed
        dependencyProvider = DependencyProvider.getInstance(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}
