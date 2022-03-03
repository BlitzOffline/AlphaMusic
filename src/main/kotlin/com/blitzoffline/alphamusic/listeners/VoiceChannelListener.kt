package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.votes.VoteType
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class VoiceChannelListener(private val bot: AlphaMusic) : ListenerAdapter() {
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
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = true
        return bot.taskManager.addLeaveTask(bot.jda, guild.id)
    }

    private fun onBotUnsuppressOrUnmute(guild: Guild, voiceState: GuildVoiceState) {
        val musicManager = bot.getMusicManager(guild)

        val channel = voiceState.channel ?: return
        if (channel.members.size < 2) return

        musicManager.player.isPaused = false
        bot.taskManager.removeLeaveTask(guild.id)
    }

    private fun onGuildVoiceLeave(guild: Guild, member: Member, channelLeft: AudioChannel) {
        val musicManager = bot.getMusicManager(guild)
        if (member.id == guild.selfMember.id) {
            musicManager.player.isPaused = true

            bot.taskManager.removeLeaveTask(guild.id)
            bot.taskManager.addClearTask(musicManager)
            musicManager.voteManager.clear()
        } else {
            VoteType.values.forEach { voteType ->
                musicManager.voteManager.getVoteManager(voteType)?.votes?.remove(member.id)
            }
            if (channelLeft != guild.selfMember.voiceState?.channel) return
            if (channelLeft.members.size >= 2) return
            if (channelLeft.members[0] != guild.selfMember) return

            bot.taskManager.addLeaveTask(bot.jda, guild.id)
        }
    }

    private fun onGuildVoiceJoin(guild: Guild, member: Member, channelJoined: AudioChannel) {
        if (member.id == guild.selfMember.id) {
            val afk = guild.afkChannel
            if (afk != null && channelJoined.id == afk.id) {
                return bot.taskManager.addLeaveTask(bot.jda, guild.id)
            }

            val musicManager = bot.getMusicManager(guild)
            musicManager.player.isPaused = false

            if (musicManager.player.playingTrack == null) {
                bot.taskManager.addLeaveTask(bot.jda, guild.id)
            }
            bot.taskManager.removeClearTask(guild.id)
        } else {
            if (channelJoined != guild.selfMember.voiceState?.channel) return
            if (guild.selfMember.voiceState?.isMuted == true) return
            if (guild.selfMember.voiceState?.isSuppressed == true) return
            bot.taskManager.removeLeaveTask(guild.id)
        }
    }
}