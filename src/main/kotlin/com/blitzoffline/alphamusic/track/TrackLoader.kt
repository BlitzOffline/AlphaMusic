package com.blitzoffline.alphamusic.track

import com.blitzoffline.alphamusic.handler.TrackLoaderResultHandler
import com.blitzoffline.alphamusic.manager.GuildManager
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.concurrent.TimeUnit

/**
 * This class is responsible for loading tracks and playlists from various sources. It contains a cache for loaded
 * items to avoid loading the same item multiple times. The cache also automatically removes items that haven't been
 * used in a while.
 */
class TrackLoader {
    val audioItemCache: Cache<String, AudioItem> = CacheBuilder
        .newBuilder()
        .maximumSize(100)
        .expireAfterWrite(25, TimeUnit.MINUTES)
        .build()

    fun loadTrack(identifier: String, guildManager: GuildManager, event: SlashCommandInteractionEvent?, deferred: Boolean = false) {
        val isUrl = URL_REGEX.matches(identifier)
        val trackUrl = when {
            isUrl -> identifier
            else -> "ytsearch:${identifier}"
        }

        val resultHandler = TrackLoaderResultHandler(event, guildManager, this, trackUrl, guildManager.jda, guildManager.guildHolder.radio(guildManager.guildId), deferred)

        val track = audioItemCache.getIfPresent(trackUrl)
            ?: run {
                guildManager.audioPlayerManager.loadItemOrdered(guildManager.audioPlayer, trackUrl, resultHandler)
                return
            }

        when (track) {
            is AudioTrack -> {
                resultHandler.trackLoaded(track)
            }
            is AudioPlaylist -> {
                resultHandler.playlistLoaded(track)
            }
        }
    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    }
}