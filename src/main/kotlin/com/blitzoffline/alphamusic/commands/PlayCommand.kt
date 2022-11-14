package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.Permission

@Command("play")
@Description("Play a song!")
class PlayCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
    )
    fun SlashSender.play(@Description("Link or keywords to find the song(s) by!") identifier: String) {
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

        bot.trackService.loadTrack(identifier, guild, event, isRadio = bot.getMusicManager(guild).audioHandler.radio, deferred = true)
    }
}