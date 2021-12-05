package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.AudioLoaderResultHandler
import com.blitzoffline.alphamusic.utils.process
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("play")
@Description("Play a song!")
class PlayCommand(private val bot: AlphaMusic) : BaseCommand() {
    private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

    @Default
    fun SlashSender.play(song: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)
        val track = if (URL_REGEX.matches(song)) song else "ytsearch:${song}"
        bot.playerManager.loadItemOrdered(musicManager.player, track, AudioLoaderResultHandler(event, musicManager, true))
    }
}