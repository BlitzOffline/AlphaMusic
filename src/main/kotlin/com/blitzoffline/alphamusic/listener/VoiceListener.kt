package com.blitzoffline.alphamusic.listener

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.vote.VoteType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class VoiceListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {
        if (event.member.id != bot.jda.selfUser.id) return

        if (event.isMuted) {
            return onBotSuppressOrMute(event.guild)
        }

        return onBotUnsuppressOrUnmute(event.guild, event.voiceState)
    }

    override fun onGuildVoiceSuppress(event: GuildVoiceSuppressEvent) {
        if (event.member.id != bot.jda.selfUser.id) return

        if (event.isSuppressed) {
            return onBotSuppressOrMute(event.guild)
        }

        return onBotUnsuppressOrUnmute(event.guild, event.voiceState)
    }

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.channelLeft != null) {
            onGuildVoiceLeave(event.guild, event.member, event.channelLeft!!)
        }

        if (event.channelJoined != null) {
            onGuildVoiceJoin(event.guild, event.member, event.channelJoined!!)
        }
    }

    private fun onBotSuppressOrMute(guild: Guild) {
        val guildManager = bot.guildManagers.getGuildManager(guild)

        guildManager.audioPlayer.isPaused = true
        return guildManager.addLeaveTask()
    }

    private fun onBotUnsuppressOrUnmute(guild: Guild, voiceState: GuildVoiceState) {
        val guildManager = bot.guildManagers.getGuildManager(guild)

        val channel = voiceState.channel ?: return
        if (channel.members.size < 2) return

        guildManager.audioPlayer.isPaused = false
        guildManager.removeLeaveTask()
    }

    private fun onGuildVoiceLeave(guild: Guild, member: Member, channelLeft: AudioChannel) {
        val guildManager = bot.guildManagers.getGuildManager(guild)
        if (member.id == guild.selfMember.id) {
            guildManager.audioPlayer.isPaused = true

            guildManager.removeLeaveTask()
            guildManager.addClearTask()
            guildManager.voteManager.clear()
        } else {
            VoteType.values.forEach { voteType ->
                guildManager.voteManager.getVoteManager(voteType)?.votes?.remove(member.id)
            }
            if (channelLeft != guild.selfMember.voiceState?.channel) return
            if (channelLeft.members.size >= 2) return
            if (channelLeft.members[0] != guild.selfMember) return

            guildManager.addLeaveTask()
        }
    }

    private fun onGuildVoiceJoin(guild: Guild, member: Member, channelJoined: AudioChannel) {
        val guildManager = bot.guildManagers.getGuildManager(guild)
        if (member.id == guild.selfMember.id) {
            val afk = guild.afkChannel

            if (afk != null && channelJoined.id == afk.id) {
                return guildManager.addLeaveTask()
            }

            guildManager.audioPlayer.isPaused = false

            if (guildManager.audioPlayer.playingTrack == null) {
                guildManager.addLeaveTask()
            }

            guildManager.removeClearTask()
        } else {
            if (channelJoined != guild.selfMember.voiceState?.channel) return
            if (guild.selfMember.voiceState?.isMuted == true) return
            if (guild.selfMember.voiceState?.isSuppressed == true) return

            guildManager.removeLeaveTask()
        }
    }
}