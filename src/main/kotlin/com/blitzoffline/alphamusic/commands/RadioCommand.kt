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
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
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