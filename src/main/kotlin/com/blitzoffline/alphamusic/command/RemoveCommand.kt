package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("remove")
@Description("Remove songs from the queue!")
class RemoveCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "not_admin"),
    )
    fun SlashCommandSender.remove(@Description("Amount of songs you want to remove. Starts from the first one in the queue!") amount: Int) {
        if (amount <= 0) {
            return event.terminate(reason = "The amount needs to be greater than 0!", ephemeral = true)
        }

        val guild = guild ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        if (guildManager.audioHandler.size() == 0) {
            return event.terminate(reason = "The queue is empty!", ephemeral = true)
        }

        if (amount >= guildManager.audioHandler.size()) {
            val removed = guildManager.audioHandler.clear()
            return event.terminate(reason = "Removed all $removed songs from the queue!")
        }

        repeat(amount) {
            guildManager.audioHandler.removeNext()
        }
        event.terminate(reason = "Removed $amount songs from the queue. ${guildManager.audioHandler.size()} songs left.")
    }
}