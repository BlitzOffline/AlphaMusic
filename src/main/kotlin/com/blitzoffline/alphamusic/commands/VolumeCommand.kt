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

@Command("volume")
@Description("Turn the volume up or down!")
class VolumeCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.volume(@Description("Number to set the volume of the bot to!") @Optional volume: Int?) {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)

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