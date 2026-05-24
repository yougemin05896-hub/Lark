package com.example.ai

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Simulates detecting scenes in video (Action, Dialogue, Scenery, etc.).
 */
class SmartSceneDetector {
    fun detectScenes(videoUri: String): Flow<String> = flow {
        delay(1500)
        emit("Scene: Introduction")
        delay(4000)
        emit("Scene: Action / Fast Movement")
        delay(3000)
        emit("Scene: Dialogue")
    }
}
