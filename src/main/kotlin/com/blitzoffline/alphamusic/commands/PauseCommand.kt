package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("pause")
@Description("Pause the audio!")
class PauseCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.pause() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)

        if (musicManager.player.isPaused) {
            return event.terminate("The audio is already paused. Use \"/resume\" to resume!")
        }

        musicManager.player.isPaused = true
        event.terminate("Paused the audio!")
    }
}