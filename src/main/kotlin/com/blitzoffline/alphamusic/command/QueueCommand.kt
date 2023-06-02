package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.track.TrackMetadata
import com.blitzoffline.alphamusic.utils.extension.terminate
import com.blitzoffline.alphamusic.utils.formatHMS
import com.github.ygimenez.method.Pages
import com.github.ygimenez.model.InteractPage
import com.github.ygimenez.model.Page
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.Duration

@Command("queue")
@Description("List all the songs that are currently queued!")
class QueueCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
    )
    fun SlashCommandSender.queue() {
        event.deferReply().queue()

        val guild = guild ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        if (guildManager.audioHandler.size() == 0) {
            return event.terminate(reason = "The queue is empty!", deferred = true)
        }

        val playing = guildManager.audioPlayer.playingTrack
        val queue = guildManager.audioHandler.queue()
        val totalPages = queue.chunked(10).size
        val pages = mutableListOf<Page>()

        for ((pageIndex, page) in queue.chunked(10).withIndex()) {
            val embed = EmbedBuilder()
                .setAuthor("Queue for ${guild.name}")

            if (playing != null) {
                embed.appendDescription(
                    """
                    __Now Playing:__
                    [${playing.info.title}](${playing.info.uri}) | `${formatHMS(Duration.ofMillis(playing.duration))} Requested by: ${playing.getUserData(
                        TrackMetadata::class.java).data.name}`
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
                    `${10 * pageIndex + trackIndex + 1}.` [${track.info.title}](${track.info.uri}) | `${formatHMS(Duration.ofMillis(track.duration))} Requested by: ${track.getUserData(
                        TrackMetadata::class.java).data.name}`
                    ${System.lineSeparator()}
                """.trimIndent()
                )
            }

            embed.appendDescription(
                """
                **${guildManager.audioHandler.size()} songs in queue | ${formatHMS(Duration.ofMillis(queue.sumOf { it.duration }))} total length**
            """.trimIndent()
            )

            val footer = StringBuilder()
            footer.append("Page ${pageIndex+1}/${totalPages} | Loop: ")
            footer.append(if (guildManager.guildHolder.loop(guild.id)) "✅" else "❌")
            footer.append(" | Radio: ")
            footer.append(if (guildManager.guildHolder.radio(guild.id)) "✅" else "❌")
            footer.append(" | Paused: ")
            footer.append(if (guildManager.audioPlayer.isPaused) "✅" else "❌")

            embed.setFooter(footer.toString())
            embed.setColor(AlphaMusic.EMBED_COLOR)
            pages.add(InteractPage.of(embed.build()))
        }

        event.interaction.hook.editOriginalEmbeds(pages[0].content as MessageEmbed).queue {
            Pages.paginate(it , pages, true)
        }
    }
}