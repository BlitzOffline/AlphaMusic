package com.blitzoffline.alphamusic.manager

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.YoutubeSourceOptions
import dev.lavalink.youtube.clients.AndroidTestsuiteWithThumbnail
import dev.lavalink.youtube.clients.MusicWithThumbnail
import dev.lavalink.youtube.clients.WebWithThumbnail


/**
 * Audio player manager used for creating audio players and loading tracks and playlists.
 */
class AudioPlayerManager(youtubeRefreshToken: String?) : DefaultAudioPlayerManager() {
    init {
        val source = YoutubeAudioSourceManager(
            YoutubeSourceOptions()
                .setAllowSearch(true)
                .setAllowDirectVideoIds(true)
                .setAllowDirectPlaylistIds(true),
            MusicWithThumbnail(), WebWithThumbnail(), AndroidTestsuiteWithThumbnail()
        )
        source.useOauth2(youtubeRefreshToken, !youtubeRefreshToken.isNullOrEmpty())
        registerSourceManager(
            source
        )

        val oldYoutubeSourceClazz = com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager::class.java
        AudioSourceManagers.registerRemoteSources(this, oldYoutubeSourceClazz)
        AudioSourceManagers.registerLocalSource(this)
    }
}