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

@Command("remove-dupes")
@Description("Remove all duplicates from the queue!")
class RemoveDupesCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "admin"),
    )
    fun SlashSender.removeDupes() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.audioHandler.size() == 0) {
            return event.terminate(reason = "There are no song queued currently!", ephemeral = true)
        }

        when(musicManager.audioHandler.removeDupes()) {
            0 -> event.terminate(reason = "No dupes found!", ephemeral = true)
            else -> event.terminate(reason = "Successfully removed all duplicates!")
        }
    }
}