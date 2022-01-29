package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("stop")
@Description("Stop the audio!")
class StopCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.stop() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null && musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The bot is not playing any audio!")
        }

        musicManager.audioHandler.queue.clear()
        musicManager.player.stopTrack()
        bot.tasksManager.addLeaveTask(guild)

        event.terminate("Stopped the audio!")
    }
}