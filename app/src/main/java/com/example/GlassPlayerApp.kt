package com.example

import android.app.Application
import com.example.di.DependencyProvider

class GlassPlayerApp : Application() {

    lateinit var dependencyProvider: DependencyProvider
        private set

    override fun onCreate() {
        super.onCreate()
        dependencyProvider = DependencyProvider.getInstance(this)
    }
}
