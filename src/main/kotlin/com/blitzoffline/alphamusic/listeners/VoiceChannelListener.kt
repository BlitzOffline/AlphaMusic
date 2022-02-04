package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.votes.VoteType
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guild = event.guild
        val musicManager = bot.getMusicManager(guild)
        if (event.member == guild.selfMember) {
            musicManager.player.isPaused = true

            bot.taskManager.removeLeaveTask(guild.id)
            bot.taskManager.addClearTask(musicManager)
            musicManager.voteHandler.clear()
        } else {
            VoteType.values.forEach { voteType ->
                musicManager.voteHandler.getVoteManager(voteType)?.votes?.remove(event.member.id)
            }
            if (event.channelLeft != guild.selfMember.voiceState?.channel) return
            if (event.channelLeft.members.size >= 2) return
            if (event.channelLeft.members[0] != guild.selfMember) return

            bot.taskManager.addLeaveTask(guild)
        }
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        if (event.member == guild.selfMember) {
            val musicManager = bot.getMusicManager(guild)
            musicManager.player.isPaused = false

            if (musicManager.player.playingTrack == null) {
                bot.taskManager.addLeaveTask(guild)
            }
            bot.taskManager.removeClearTask(guild.id)
        } else {
            if (event.channelJoined != guild.selfMember.voiceState?.channel) return

            bot.taskManager.removeLeaveTask(guild.id)
        }
    }
}