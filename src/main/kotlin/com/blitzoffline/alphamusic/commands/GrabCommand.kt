package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.asEmbedNullable
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("grab")
@Description("Get a DM listing the currently playing song!")
class GrabCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
    )
    fun SlashCommandSender.grab() {
        deferReply().queue()

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)
        val playing = musicManager.player.playingTrack.asEmbedNullable(icon = user.avatarUrl, showTimestamp = false)
            ?: return event.terminate(reason = "There is no song playing currently!", ephemeral = true, deferred = true)

        event.user.openPrivateChannel()
            .flatMap { it.sendMessageEmbeds(playing) }
            .queue(
                {
                    event.terminate("Check your DMs! Currently playing song was listed there.", ephemeral = true, deferred = true)
                }
            ) {
                event.terminate("Something went wrong while grabbing. Make sure your DMs are not closed!", ephemeral = true, deferred = true)
            }
    }
}