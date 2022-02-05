package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMS
import com.blitzoffline.alphamusic.utils.terminate
import com.github.ygimenez.method.Pages
import com.github.ygimenez.model.InteractPage
import com.github.ygimenez.model.Page
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

@Command("queue")
@Description("List all the songs that are currently queued!")
class QueueCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("IN_GUILD", messageKey = "IN_GUILD"),
        Requirement("BOT_IS_IN_VC", messageKey = "BOT_IS_IN_VC"),
    )
    fun SlashSender.queue() {
        event.deferReply().queue()

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The queue is empty!", deferred = true)
        }

        val playing = musicManager.player.playingTrack
        val totalPages = musicManager.audioHandler.queue.chunked(10).size
        val pages = mutableListOf<Page>()

        for ((pageIndex, page) in musicManager.audioHandler.queue.chunked(10).withIndex()) {
            val embed = EmbedBuilder()
                .setAuthor("Queue for ${guild.name}")

            if (playing != null) {
                embed.appendDescription(
                    """
                    __Now Playing:__
                    [${playing.info.title}](${playing.info.uri}) | `${formatHMS(Duration.ofMillis(playing.duration))} Requested by: ${playing.getUserData(TrackMetadata::class.java).data.name}`
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

            for ((trackIndex, track) in page.withIndex()) {
                embed.appendDescription(
                    """
                    `${10 * pageIndex + trackIndex + 1}.` [${track.info.title}](${track.info.uri}) | `${formatHMS(Duration.ofMillis(track.duration))} Requested by: ${track.getUserData(TrackMetadata::class.java).data.name}`
                    ${System.lineSeparator()}
                """.trimIndent()
                )
            }

            embed.appendDescription(
                """
                **${musicManager.audioHandler.queue.size} songs in queue | ${formatHMS(Duration.ofMillis(musicManager.audioHandler.queue.sumOf { it.duration }))} total length**
            """.trimIndent()
            )

            val footer = StringBuilder()
            footer.append("Page ${pageIndex+1}/${totalPages} | Loop: ")
            footer.append(if (musicManager.audioHandler.loop) "✅" else "❌")
            footer.append(" | Radio: ")
            footer.append(if (musicManager.audioHandler.radio) "✅" else "❌")
            footer.append(" | Paused: ")
            footer.append(if (musicManager.player.isPaused) "✅" else "❌")

            embed.setFooter(footer.toString())
            pages.add(InteractPage(embed.build()))
        }

        event.interaction.hook.editOriginalEmbeds(pages[0].content as MessageEmbed).queue {
            Pages.paginate(it , pages, true)
        }
    }
}