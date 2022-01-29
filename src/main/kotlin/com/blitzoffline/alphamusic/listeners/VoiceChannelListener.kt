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

            bot.tasksManager.removeLeaveTask(guild.id)
            bot.tasksManager.addClearTask(musicManager)
            musicManager.votesManager.clear()
        } else {
            VoteType.values.forEach { voteType ->
                musicManager.votesManager.getVoteManager(voteType)?.votes?.remove(event.member.id)
            }
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

            if (musicManager.player.playingTrack == null) {
                bot.tasksManager.addLeaveTask(guild)
            }
            bot.tasksManager.removeClearTask(guild.id)
        } else {
            if (event.channelJoined != guild.selfMember.voiceState?.channel) return

            bot.tasksManager.removeLeaveTask(guild.id)
        }
    }
}