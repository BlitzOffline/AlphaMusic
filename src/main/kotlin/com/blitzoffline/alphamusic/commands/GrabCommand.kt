package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMSDouble
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.progressBar
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder


@Command("grab")
@Description("Get a DM listing the currently playing song!")
class GrabCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.grab() {
        if (!process()) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getGuildMusicManager(guild)
        val playing = musicManager.playing() ?: return event.terminate("There is no song playing currently!")

        event.user.openPrivateChannel().flatMap { channel -> channel.sendMessageEmbeds(playing) }
            .queue({ event.terminate("Check your DMs! Currently playing song was listed there.") })
            { event.terminate("Something went wrong while grabbing. Make sure your DMs are not closed!") }
    }
}