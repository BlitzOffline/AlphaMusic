package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.asEmbedNullable
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("np")
@Description("List the currently playing song!")
class NowPlayingCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
    )
    fun SlashCommandSender.nowPlaying() {
        deferReply().queue()
        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)
        val playing = musicManager.player.playingTrack.asEmbedNullable(icon = user.avatarUrl, showTimestamp = true)
            ?: return event.terminate(reason = "There is no song playing currently!", deferred = true)

        event.terminate(playing, deferred = true)
    }
}