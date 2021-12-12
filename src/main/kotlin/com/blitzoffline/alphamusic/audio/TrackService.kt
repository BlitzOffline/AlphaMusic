package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.concurrent.TimeUnit
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class TrackService(private val bot: AlphaMusic) {
    val audioItemCache: Cache<String, AudioItem> = CacheBuilder
        .newBuilder()
        .maximumSize(100)
        .expireAfterWrite(25, TimeUnit.MINUTES)
        .build()

    fun loadTrack(identifier: String, guild: Guild, event: SlashCommandEvent, deferred: Boolean = false) {
        val isUrl = URL_REGEX.matches(identifier)
        val musicManager = bot.getMusicManager(guild)
        val trackUrl = if (isUrl) identifier else "ytsearch:${identifier}"

        val resultHandler = LoaderResultHandler(event, musicManager, this, trackUrl, deferred)

        val track = audioItemCache.getIfPresent(trackUrl)
            ?: run {
                bot.playerManager.loadItemOrdered(musicManager.player, trackUrl, LoaderResultHandler(event, musicManager, this, trackUrl, deferred))
                return
            }

        when (track) {
            is AudioTrack -> resultHandler.trackLoaded(track)
            is AudioPlaylist -> resultHandler.playlistLoaded(track)
        }

    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    }
}