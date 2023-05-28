package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.*
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("volume")
@Description("Turn the volume up or down!")
class VolumeCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("admin", messageKey = "not_admin"),
    )
    fun SlashCommandSender.volume(@Description("Number to set the volume of the bot to!") @Optional volume: Int?) {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (volume == null) {
            return event.terminate(reason = "Volume is: ${musicManager.player.volume}", ephemeral = true)
        }

        if (volume > 150 || volume < 0) {
            return event.terminate(reason = "Volume needs to be a value between 0 and 150!", ephemeral = true)
        }

        musicManager.player.volume = volume
        event.terminate(reason = "Volume set to: $volume!")
    }
}