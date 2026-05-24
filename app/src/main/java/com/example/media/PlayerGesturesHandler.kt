package com.example.media

/**
 * Calculates gesture swipe differences to map to Volume / Brightness.
 */
class PlayerGesturesHandler {
    
    // Y-axis drag gesture translates to volume/brightness
    fun calculateAdjustment(dragAmountY: Float, maxScreenHeight: Float): Float {
        val sensitivity = 1.5f
        return -(dragAmountY / maxScreenHeight) * sensitivity
    }
}
