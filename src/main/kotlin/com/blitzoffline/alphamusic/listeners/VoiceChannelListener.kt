package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.votes.VoteType
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

// todo: check for bot deafening/undeafening as well.
class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.channelLeft != null) {
            onGuildVoiceLeave(event.guild, event.member, event.channelLeft!!)
        }

        if (event.channelJoined != null) {
            onGuildVoiceJoin(event.guild, event.member, event.channelJoined!!)
        }
    }

    private fun onGuildVoiceLeave(guild: Guild, member: Member, channelLeft: AudioChannel) {
        val musicManager = bot.getMusicManager(guild)
        if (member == guild.selfMember) {
            musicManager.player.isPaused = true

            bot.taskManager.removeLeaveTask(guild.id)
            bot.taskManager.addClearTask(musicManager)
            musicManager.voteHandler.clear()
        } else {
            VoteType.values.forEach { voteType ->
                musicManager.voteHandler.getVoteManager(voteType)?.votes?.remove(member.id)
            }
            if (channelLeft != guild.selfMember.voiceState?.channel) return
            if (channelLeft.members.size >= 2) return
            if (channelLeft.members[0] != guild.selfMember) return

            bot.taskManager.addLeaveTask(guild)
        }
    }

    private fun onGuildVoiceJoin(guild: Guild, member: Member, channelJoined: AudioChannel) {
        if (member == guild.selfMember) {
            val musicManager = bot.getMusicManager(guild)
            musicManager.player.isPaused = false

            if (musicManager.player.playingTrack == null) {
                bot.taskManager.addLeaveTask(guild)
            }
            bot.taskManager.removeClearTask(guild.id)
        } else {
            if (channelJoined != guild.selfMember.voiceState?.channel) return

            bot.taskManager.removeLeaveTask(guild.id)
        }
    }
}