package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import java.util.Timer
import kotlin.concurrent.schedule
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guild = event.guild
        if (event.member == guild.selfMember) {
            val musicManager = bot.getMusicManager(guild)
            musicManager.player.isPaused = true

            bot.tasksManager.removeLeaveTask(guild.id)
            bot.tasksManager.addClearTask(musicManager)
        } else {
            if (event.channelLeft != guild.selfMember.voiceState?.channel) return
            if (event.channelLeft.members.size >= 2) return
            if (event.channelLeft.members[0] != guild.selfMember) return

            bot.tasksManager.addLeaveTask(guild)
        }
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        if (event.member == guild.selfMember) {
            val musicManager = bot.getMusicManager(guild)
            musicManager.player.isPaused = false

            bot.tasksManager.addLeaveTask(guild)
            bot.tasksManager.removeClearTask(guild.id)
        } else {
            if (event.channelJoined != guild.selfMember.voiceState?.channel) return

            bot.tasksManager.removeLeaveTask(guild.id)
        }
    }
}