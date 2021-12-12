package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("shuffle")
@Description("Shuffle the queue!")
class ShuffleCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.shuffle() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        when (musicManager.audioHandler.queue.size) {
            0 -> event.terminate("There are no songs in the queue to be shuffled.")
            1 -> event.terminate("There is only one song in the queue.")
            else -> musicManager.audioHandler.shuffle()
        }

        event.terminate("The queue was successfully shuffled.")
    }
}