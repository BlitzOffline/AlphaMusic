package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.asEmbed
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("np")
@Description("List currently playing song!")
class NowPlayingCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.nowPlaying() {
        deferReply().queue()
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)
        val playing = musicManager.player.playingTrack.asEmbed(icon = user.avatarUrl, showTimestamp = true)
            ?: return event.terminate("There is no song playing currently!", deferred = true)

        event.terminate(playing, deferred = true)
    }
}