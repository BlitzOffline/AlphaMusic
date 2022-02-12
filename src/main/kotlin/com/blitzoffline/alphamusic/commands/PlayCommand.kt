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
        event.deferReply().queue()
        // todo: Make bot not join the voice channel unless there was a song found.

        val guild = guild ?: return
        val member = member ?: return
        val memberChannel = member.voiceState?.channel

        if (guild.selfMember.voiceState?.channel == null) {
            if (memberChannel == null) {
                return event.terminate("You need to be in a voice channel!", deferred = true)
            }

            if (kotlin.runCatching { guild.audioManager.openAudioConnection(memberChannel) }.isFailure) {
                return event.terminate("Could not connect to your voice channel!", deferred = true)
            }
        }

        if (guild.selfMember.voiceState?.channel != memberChannel && !member.hasPermission(Permission.ADMINISTRATOR)) {
            return event.terminate("You need to be in the same Voice Channel as the bot to do this!", deferred = true)
        }

        bot.trackService.loadTrack(identifier, guild, event, isRadio = bot.getMusicManager(guild).audioHandler.radio, deferred = true)
    }
}