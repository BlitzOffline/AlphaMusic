package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("clear")
@Description("Clear the queue!")
class ClearCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.clear() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)

        if (musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The queue is already empty!")
        }

        musicManager.audioHandler.queue.clear()
        event.terminate("Cleared the queue!")
    }
}