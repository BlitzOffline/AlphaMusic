package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("resume")
@Description("Resume the audio!")
class ResumeCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.resume() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (!musicManager.player.isPaused) {
            return event.terminate("The audio is not paused. Use \"/pause\" to pause!")
        }

        musicManager.player.isPaused = false
        bot.tasksManager.removeLeaveTask(guild.id)
        event.terminate("Resumed the audio!")
    }
}