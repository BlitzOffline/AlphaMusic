package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("resume")
@Description("Resume the audio!")
class ResumeCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
        Requirement("paused", messageKey = "not_paused"),
        Requirement("requester_or_admin", messageKey = "not_requester_or_admin"),
    )
    fun SlashCommandSender.resume() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = false
        event.terminate(reason = "Resumed the audio!")
    }
}