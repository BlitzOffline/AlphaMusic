package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("np")
@Description("List currently playing song!")
class NowPlayingCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.nowPlaying() {
        if (!process()) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)
        val playing = musicManager.playing() ?: return event.terminate("There is no song playing currently!")

        event.terminate(playing)
    }
}