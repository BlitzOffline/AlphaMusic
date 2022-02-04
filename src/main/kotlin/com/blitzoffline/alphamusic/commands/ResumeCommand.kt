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
        Requirement("IN_GUILD", messageKey = "IN_GUILD"),
        Requirement("BOT_IS_IN_VC", messageKey = "BOT_IS_IN_VC"),
        Requirement("SAME_CHANNEL_OR_ADMIN", messageKey = "SAME_CHANNEL_OR_ADMIN"),
        Requirement("IS_PAUSED", messageKey = "IS_PAUSED"),
        Requirement("IS_REQUESTER_OR_ADMIN", messageKey = "IS_REQUESTER_OR_ADMIN"),
    )
    fun SlashSender.resume() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        musicManager.player.isPaused = false
        bot.taskManager.removeLeaveTask(guild.id)

        event.terminate("Resumed the audio!")
    }
}