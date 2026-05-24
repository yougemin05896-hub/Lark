package com.example.media

import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer

/**
 * Handles injecting and parsing subtitles into ExoPlayer dynamically.
 */
class SubtitleManager(private val player: ExoPlayer) {

    fun injectSubtitle(videoUri: String, subtitleUri: String, language: String = "en", mimeType: String = MimeTypes.APPLICATION_SUBRIP) {
        val subtitle = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(subtitleUri))
            .setMimeType(mimeType)
            .setLanguage(language)
            .setSelectionFlags(androidx.media3.common.C.SELECTION_FLAG_DEFAULT)
            .build()
            
        val mediaItem = MediaItem.Builder()
            .setUri(videoUri)
            .setSubtitleConfigurations(listOf(subtitle))
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
    }
}
