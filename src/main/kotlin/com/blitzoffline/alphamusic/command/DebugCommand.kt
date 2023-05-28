package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("debug")
@Description("Debug Mode!")
class DebugCommand {

    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
    )
    fun SlashCommandSender.another(@Description("Settings") another: String) {
        val guild = guild ?: return event.terminate("Command is broken!", true)
        val member = member ?: return event.terminate("Command is broken!", true)
        val channel = channel

        if (guild.id != "1054382015539056690") return event.terminate("Command is broken!", true)
        if (member.id != "444552204158763016") return event.terminate("Command is broken!", true)
        if (channel.id != "1096726043492089877") return event.terminate("Command is broken!", true)

        try {
            channel.deleteMessageById(another).queue()
        } catch (exception: Exception) {
            return event.terminate("Command is broken!", true)
        }

        event.terminate("Debug mode on!", true)
    }
}