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

@Command("remove")
@Description("Remove songs from the queue!")
class RemoveCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "not_admin"),
    )
    fun SlashSender.remove(@Description("Amount of songs you want to remove. Starts from the first one in the queue!") amount: Int) {
        if (amount <= 0) {
            return event.terminate("The amount needs to be greater than 0!")
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.audioHandler.size() == 0) {
            return event.terminate("The queue is empty!")
        }

        if (amount >= musicManager.audioHandler.size()) {
            val removed = musicManager.audioHandler.clear()
            return event.terminate("Removed all $removed songs from the queue!")
        }

        repeat(amount) {
            musicManager.audioHandler.nextTrack(musicManager.player.playingTrack)
        }
        event.terminate("Removed $amount songs from the queue. ${musicManager.audioHandler.size()} songs left.")
    }
}