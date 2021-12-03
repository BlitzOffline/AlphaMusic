package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("queue")
@Description("List all the songs that are currently queued!")
class QueueCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.queue() {
        if (!process()) {
            return
        }

        val guild = event.guild
    }
}