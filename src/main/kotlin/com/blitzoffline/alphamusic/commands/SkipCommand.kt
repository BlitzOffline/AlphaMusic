package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("skip")
@Description("Skip songs!")
class SkipCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.skip(@Description("Amount of songs to skip. Defaults to 1!") @Optional amount: Int?)  {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val finalAmount = amount ?: 1
        val musicManager = bot.getMusicManager(guild)
        val playing = musicManager.player.playingTrack ?: run {
            return event.terminate("There are no songs currently playing")
        }

        if (finalAmount <= 0) {
            return event.terminate("Make sure you enter a positive integer!")
        }

        if (finalAmount == 1 && musicManager.player.playingTrack != null) {
            musicManager.audioHandler.nextTrack(playing)
            return event.terminate("Skipped currently playing song!")
        }

        val available = if (musicManager.player.playingTrack != null) musicManager.audioHandler.queue.size + 1 else musicManager.audioHandler.queue.size
        if (finalAmount > available) {
            musicManager.audioHandler.queue.clear()
            musicManager.audioHandler.nextTrack(playing)
            return event.terminate("Skipped $available song(s). No songs left in the queue!")
        }

        repeat(finalAmount) {
            musicManager.audioHandler.nextTrack(playing)
        }
        event.terminate("Skipped $finalAmount song(s)!")
    }
}