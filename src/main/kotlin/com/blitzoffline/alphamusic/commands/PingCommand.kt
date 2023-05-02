package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.EmbedBuilder

@Command("ping")
@Description("Pong. Hopefully...")
class PingCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild")
    )
    fun SlashCommandSender.ping() {
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