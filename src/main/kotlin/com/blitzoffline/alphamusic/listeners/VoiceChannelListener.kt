package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import java.util.Timer
import java.util.TimerTask
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.concurrent.schedule

class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
    private var clearTasks = hashMapOf<String, TimerTask>()
    private var leaveTasks = hashMapOf<String, TimerTask>()

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guild = event.guild
        if (event.member == guild.selfMember) {
            val musicManager = bot.getGuildMusicManager(guild)
            musicManager.player.isPaused = true

            clearTasks[guild.id] = Timer().schedule(3000000) {
                musicManager.audioHandler.queue.clear()
                musicManager.audioHandler.nextTrack()
                clearTasks[guild.id]?.cancel()
                clearTasks.remove(guild.id)
            }
        } else {
            if (event.channelLeft != guild.selfMember.voiceState?.channel) return
            if (event.channelLeft.members.size >= 2) return
            if (event.channelLeft.members[0] != guild.selfMember) return

            leaveTasks[guild.id] = Timer().schedule(3000000) {
                guild.audioManager.closeAudioConnection()
                leaveTasks[guild.id]?.cancel()
                leaveTasks.remove(guild.id)
            }
        }
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        if (event.member == guild.selfMember) {
            val musicManager = bot.getGuildMusicManager(guild)
            musicManager.player.isPaused = false

            clearTasks[guild.id]?.cancel()
            clearTasks.remove(guild.id)
        } else {
            if (event.channelJoined != guild.selfMember.voiceState?.channel) return

            leaveTasks[guild.id]?.cancel()
            leaveTasks.remove(guild.id)
        }
    }
}