package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("play")
@Description("Play a song!")
class PlayCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.play(@Description("Link or keywords to find the song(s) by!") identifier: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return

        bot.trackService.loadTrack(identifier, guild, event, true)
    }
}