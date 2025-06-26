package com.blitzoffline.alphamusic.manager

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.YoutubeSourceOptions
import dev.lavalink.youtube.clients.TvHtml5EmbeddedWithThumbnail


/**
 * Audio player manager used for creating audio players and loading tracks and playlists.
 */
class AudioPlayerManager(youtubeRefreshToken: String?) : DefaultAudioPlayerManager() {
    init {
//        TODO: Fix youtube requiring login
        val source = YoutubeAudioSourceManager(
            YoutubeSourceOptions()
                .setAllowSearch(true)
                .setAllowDirectVideoIds(true)
                .setAllowDirectPlaylistIds(true),
            TvHtml5EmbeddedWithThumbnail()
//            MusicWithThumbnail(), WebWithThumbnail(), AndroidMusicWithThumbnail(), TvHtml5EmbeddedWithThumbnail()
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