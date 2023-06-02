package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("remove-dupes")
@Description("Remove all duplicates from the queue!")
class RemoveDupesCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "admin"),
    )
    fun SlashCommandSender.removeDupes() {
        val guild = guild ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        if (guildManager.audioHandler.size() == 0) {
            return event.terminate(reason = "There are no song queued currently!", ephemeral = true)
        }

        when(guildManager.audioHandler.removeDupes()) {
            0 -> event.terminate(reason = "No dupes found!", ephemeral = true)
            else -> event.terminate(reason = "Successfully removed all duplicates!")
        }
    }
}