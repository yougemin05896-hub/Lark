package com.example.media

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer

/**
 * Handles audio frequencies, bass boost, and standard equalization.
 */
class AudioEffectsController(audioSessionId: Int) {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    init {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoostStrength(strength: Short) {
        bassBoost?.setStrength(strength)
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
    }
}
