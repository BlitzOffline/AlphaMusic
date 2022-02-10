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

@Command("resume")
@Description("Resume the audio!")
class ResumeCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
        Requirement("paused", messageKey = "not_paused"),
        Requirement("requester_or_admin", messageKey = "not_requester_or_admin"),
    )
    fun SlashSender.resume() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = false
        bot.taskManager.removeLeaveTask(guild.id)

        event.terminate("Resumed the audio!")
    }
}