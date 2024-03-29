package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import jdk.jfr.Description

@Command("pause")
@Description("Pause the audio!")
class PauseCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
        Requirement("paused", messageKey = "paused", invert = true),
        Requirement("requester_or_admin", messageKey = "not_requester_or_admin"),
    )
    fun SlashCommandSender.pause() {
        val guild = guild ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        guildManager.audioPlayer.isPaused = true
        event.terminate(reason = "Paused the audio!")
    }
}