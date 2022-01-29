package com.blitzoffline.alphamusic.audio

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager

class PlayerManager(youtubeEmail: String?, youtubePass: String?) : DefaultAudioPlayerManager() {
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