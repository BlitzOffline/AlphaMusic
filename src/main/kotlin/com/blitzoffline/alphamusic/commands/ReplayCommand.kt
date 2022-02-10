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

@Command("replay")
@Description("Mark a song to be replayed!")
class ReplayCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.replay() {
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null) {
            return event.terminate("There is no song currently playing to be replayed!")
        }

        if (musicManager.audioHandler.replay) {
            return event.terminate("Currently playing song is already marked to be replayed!")
        }

        musicManager.audioHandler.replay = true
        event.terminate("Currently playing song marked to be replayed!")
    }
}