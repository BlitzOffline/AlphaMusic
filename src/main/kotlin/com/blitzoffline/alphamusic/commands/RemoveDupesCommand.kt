package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("remove-dupes")
@Description("Remove all duplicates from the queue!")
class RemoveDupesCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.removeDupes() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("There are no song queued currently!")
        }

        when(musicManager.audioHandler.removeDupes()) {
            0 -> event.terminate("No dupes found!")
            else -> event.terminate("Successfully removed all duplicates!")
        }
    }
}