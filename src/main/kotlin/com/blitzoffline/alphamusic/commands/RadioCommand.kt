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
import net.dv8tion.jda.api.Permission

@Command("radio")
@Description("Toggle radio!")
class RadioCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("IN_GUILD", messageKey = "IN_GUILD"),
        Requirement("BOT_IS_IN_VC", messageKey = "BOT_IS_IN_VC"),
        Requirement("SAME_CHANNEL_OR_ADMIN", messageKey = "SAME_CHANNEL_OR_ADMIN")
    )
    fun SlashSender.loop() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            musicManager.audioHandler.radio = !musicManager.audioHandler.radio
            return if (musicManager.audioHandler.radio) {
                event.terminate("Radio mode has been enabled!")
            } else {
                event.terminate("Radio mode has been disabled!")
            }
        }

        musicManager.audioHandler.radio = !musicManager.audioHandler.radio
        return if (musicManager.audioHandler.radio) {
            event.terminate("Radio mode has been enabled!")
        } else {
            event.terminate("Radio mode has been disabled!")
        }
    }
}