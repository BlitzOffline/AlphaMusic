package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("volume")
@Description("Turn the volume up or down!")
class VolumeCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.volume(@Description("Number to set the volume of the bot to!") @Optional volume: Int?) {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (volume == null) {
            return event.terminate("Volume is: ${musicManager.player.volume}")
        }

        if (volume > 150 || volume < 0) {
            return event.terminate("Volume needs to be a value between 0 and 150!")
        }

        musicManager.player.volume = volume
        event.terminate("Volume set to: $volume!")
    }
}