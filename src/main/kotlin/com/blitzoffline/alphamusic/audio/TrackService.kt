package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.concurrent.TimeUnit
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class TrackService(private val bot: AlphaMusic) {
    val audioItemCache: Cache<String, AudioItem> = CacheBuilder
        .newBuilder()
        .maximumSize(100)
        .expireAfterWrite(25, TimeUnit.MINUTES)
        .build()

    fun loadTrack(identifier: String, guild: Guild, event: SlashCommandInteractionEvent?, isRadio: Boolean, deferred: Boolean = false) : Boolean {
        val isUrl = URL_REGEX.matches(identifier)
        val musicManager = bot.getMusicManager(guild)
        val trackUrl = when {
            isUrl -> identifier
            else -> "ytsearch:${identifier}"
        }

        val resultHandler = LoaderResultHandler(event, musicManager, this, trackUrl, bot.jda, isRadio, deferred)

        val track = audioItemCache.getIfPresent(trackUrl)
            ?: run {
                bot.playerManager.loadItemOrdered(musicManager.player, trackUrl, resultHandler).get()
                return resultHandler.success
            }

        return when (track) {
            is AudioTrack -> {
                resultHandler.trackLoaded(track)
                true
            }
            is AudioPlaylist -> {
                resultHandler.playlistLoaded(track)
                true
            }
            else -> false
        }

    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    }
}