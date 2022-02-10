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

@Command("pause")
@Description("Pause the audio!")
class PauseCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
        Requirement("not_paused", messageKey = "paused"),
        Requirement("requester_or_admin", messageKey = "not_requester_or_admin"),
    )
    fun SlashSender.pause() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = true
        bot.taskManager.addLeaveTask(guild)

        event.terminate("Paused the audio!")
    }
}