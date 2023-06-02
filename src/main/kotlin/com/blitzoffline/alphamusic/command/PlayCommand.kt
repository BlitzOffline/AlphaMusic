package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.track.TrackLoader
import com.blitzoffline.alphamusic.utils.extension.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.Permission

@Command("play")
@Description("Play a song!")
class PlayCommand(private val trackLoader: TrackLoader, private val audioManagers: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
    )
    fun SlashCommandSender.play(@Description("Link or keywords to find the song(s) by!") identifier: String) {
        val guild = guild ?: return
        val member = member ?: return
        val memberChannel = member.voiceState?.channel

        if (guild.selfMember.voiceState?.channel == null && memberChannel == null) {
            return event.terminate(reason = "You need to be in a voice channel!", ephemeral = true)
        }

        if (guild.selfMember.voiceState?.channel != null && memberChannel != guild.selfMember.voiceState?.channel && !member.hasPermission(Permission.ADMINISTRATOR)) {
            return event.terminate(reason = "You need to be in the same Voice Channel as the bot to do this!", ephemeral = true)
        }

        event.deferReply().queue()

        if (guild.selfMember.voiceState?.channel == null
            && kotlin.runCatching { guild.audioManager.openAudioConnection(memberChannel) }.isFailure) {
            return event.terminate(reason = "Could not connect to your voice channel!", ephemeral = true, deferred = true)
        }

        trackLoader.loadTrack(
            identifier = identifier,
            guildManager = audioManagers.getGuildManager(guild),
            event = event,
            deferred = true
        )
    }
}