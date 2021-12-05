package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.member.id != bot.jda.selfUser.id) {
            return
        }

        val musicManager = bot.getGuildMusicManager(event.guild)
        if (musicManager.player.isPaused) {
            return
        }

        musicManager.player.isPaused = true
        // todo: Wait 5 minutes and if the bot is not in a voice channel by then clear the queue
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (event.member.id != bot.jda.selfUser.id) {
            return
        }

        val musicManager = bot.getGuildMusicManager(event.guild)
        if (!musicManager.player.isPaused) {
            return
        }

        musicManager.player.isPaused = false
    }
}