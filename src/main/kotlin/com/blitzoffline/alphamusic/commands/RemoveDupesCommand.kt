package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("remove-dupes")
@Description("Remove all duplicates from the queue!")
class RemoveDupesCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "admin"),
    )
    fun SlashCommandSender.removeDupes() {
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