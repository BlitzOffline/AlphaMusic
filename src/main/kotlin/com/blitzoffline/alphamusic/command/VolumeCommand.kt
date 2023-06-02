package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import dev.triumphteam.cmd.core.annotations.*
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("volume")
@Description("Turn the volume up or down!")
class VolumeCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "not_admin"),
    )
    fun SlashCommandSender.volume(@Description("Number to set the volume of the bot to!") @Optional volume: Int?) {
        val guild = guild ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        if (volume == null) {
            return event.terminate(reason = "Volume is: ${guildManager.guildHolder.volume(guild.id)}", ephemeral = true)
        }

        if (volume > 150 || volume < 0) {
            return event.terminate(reason = "Volume needs to be a value between 0 and 150!", ephemeral = true)
        }

        guildManager.guildHolder.setVolume(guild.id, volume)
        guildManager.audioPlayer.volume = guildManager.guildHolder.volume(guild.id)
        event.terminate(reason = "Volume set to: $volume!")
    }
}