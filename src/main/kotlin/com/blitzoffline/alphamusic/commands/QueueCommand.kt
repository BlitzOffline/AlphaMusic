package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMS
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder

@Command("queue")
@Description("List all the songs that are currently queued!")
class QueueCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.queue() {
        if (!process()) {
            return
        }

        val guild = event.guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)

        if (musicManager.audioHandler.queue.size == 0) {
            return event.terminate("The queue is empty!.", ephemeral = false)
        }

        val nowPlaying = musicManager.player.playingTrack

        val embed = EmbedBuilder()
            .setAuthor("Queue for ${guild.name}")

        if (nowPlaying != null) {
            embed.appendDescription(
                """
                    __Now Playing:__
                    [${nowPlaying.info.title}](${nowPlaying.info.uri}) | `${formatHMS(Duration.ofMillis(nowPlaying.duration))} Requested by: ${nowPlaying.getUserData(TrackMetadata::class.java).data.name}`
                    ${System.lineSeparator()}
                """.trimIndent()
            )
        }

        embed.appendDescription(
            """
                __Up Next:__
                ${System.lineSeparator()}
            """.trimIndent()
        )

        // todo: display songs based on page you are on.

        val pages = musicManager.audioHandler.queue.chunked(10)

        val pageNumber = 0
        val page = pages[pageNumber]

        var item = 1
        page.forEach { track ->
            embed.appendDescription(
                """
                    `$item.` [${track.info.title}](${track.info.uri}) | `${formatHMS(Duration.ofMillis(track.duration))} Requested by: ${track.getUserData(TrackMetadata::class.java).data.name}`
                    ${System.lineSeparator()}
                """.trimIndent()
            )
            item++
        }

        val total = musicManager.audioHandler.queue.sumOf { it.duration }

        embed.appendDescription(
            """
                **${musicManager.audioHandler.queue.size} songs in queue | ${formatHMS(Duration.ofMillis(total))} total length**
            """.trimIndent()
        )

        val footer = StringBuilder()
        footer.append("Page ${pageNumber+1}/${pages.size} | Loop: ")
        if (musicManager.audioHandler.loop) {
            footer.append("✅")
        } else {
            footer.append("❌")
        }

        footer.append(" | Paused: ")

        if (musicManager.player.isPaused) {
            footer.append("✅")
        } else {
            footer.append("❌")
        }

        embed.setFooter(footer.toString())

        event.terminate(embed.build())
    }
}