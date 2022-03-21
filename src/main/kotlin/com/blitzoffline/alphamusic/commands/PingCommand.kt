package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.EmbedBuilder

@Command("ping")
@Description("Pong. Hopefully...")
class PingCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild")
    )
    fun SlashSender.ping() {
        deferReply().queue()

        val embed = EmbedBuilder()
            .setColor(AlphaMusic.EMBED_COLOR)
            .setTitle("Pong!")
            .setDescription("""
                • Heartbeat response time: ${bot.jda.gatewayPing}ms
            """.trimIndent())

        bot.jda.restPing.queue { ping ->
            embed.appendDescription(
                """
                    ${System.lineSeparator()}• Rest ping time: ${ping}ms
                """.trimIndent()
            )

            event.terminate(embed.build(), ephemeral = false, deferred = true)
        }
    }
}