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
        val nowPlaying = musicManager.player.playingTrack
            ?: return event.terminate("There is no song playing currently!")

        event.terminate(
             EmbedBuilder()
                .setAuthor("Now Playing ♪", null, bot.jda.selfUser.avatarUrl)
                .setTitle(nowPlaying.info.title, nowPlaying.info.uri)
                .setThumbnail(nowPlaying.info.artworkUrl)
                .setDescription("""
                    `${progressBar(nowPlaying.position, nowPlaying.duration)}`
                    
                    `${formatHMSDouble(Duration.ofMillis(nowPlaying.position), Duration.ofMillis(nowPlaying.duration))}`
                    
                    `Requested by:` ${nowPlaying.getUserData(TrackMetadata::class.java).data.name}
                """.trimIndent())
                .build(),
        )
    }
}