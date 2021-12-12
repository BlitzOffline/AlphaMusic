package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.asEmbed
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("grab")
@Description("Get a DM listing the currently playing song!")
class GrabCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.grab() {
        deferReply().queue()
        if (!process(deferred = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)
        val playing = musicManager.player.playingTrack.asEmbed(guild.selfMember.avatarUrl)
            ?: return event.terminate("There is no song playing currently!", deferred = true)

        event.user.openPrivateChannel()
            .flatMap { it.sendMessageEmbeds(playing) }
            .queue(
                {
                    event.terminate("Check your DMs! Currently playing song was listed there.", deferred = true)
                }
            ) {
                event.terminate("Something went wrong while grabbing. Make sure your DMs are not closed!", deferred = true)
            }
    }
}