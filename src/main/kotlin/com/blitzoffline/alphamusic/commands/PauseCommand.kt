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
        Requirement("IN_GUILD", messageKey = "IN_GUILD"),
        Requirement("BOT_IS_IN_VC", messageKey = "BOT_IS_IN_VC"),
        Requirement("SAME_CHANNEL_OR_ADMIN", messageKey = "SAME_CHANNEL_OR_ADMIN"),
        Requirement("IS_NOT_PAUSED", messageKey = "IS_NOT_PAUSED"),
        Requirement("IS_REQUESTER_OR_ADMIN", messageKey = "IS_REQUESTER_OR_ADMIN"),
    )
    fun SlashSender.pause() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = true
        bot.tasksManager.addLeaveTask(guild)

        event.terminate("Paused the audio!")
    }
}