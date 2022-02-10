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

@Command("stop")
@Description("Stop the audio!")
class StopCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.stop() {
        // todo: Make the bot also leave on stop.
        val guild = guild ?: return

        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null && musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The bot is not playing any audio!")
        }

        musicManager.audioHandler.queue.clear()
        musicManager.player.stopTrack()
        bot.taskManager.addLeaveTask(guild)

        event.terminate("Stopped the audio!")
    }
}