package com.blitzoffline.alphamusic.manager

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager

/**
 * Audio player manager used for creating audio players and loading tracks and playlists.
 */
class AudioPlayerManager(youtubeEmail: String?, youtubePass: String?) : DefaultAudioPlayerManager() {
    init {
        if (youtubeEmail != null && youtubePass != null) {
            registerSourceManager(
                YoutubeAudioSourceManager(true, youtubeEmail, youtubePass)
            )
        }

        AudioSourceManagers.registerRemoteSources(this)
        AudioSourceManagers.registerLocalSource(this)
    }
}