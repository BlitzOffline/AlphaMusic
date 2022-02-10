package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Instant
import net.dv8tion.jda.api.EmbedBuilder

@Command("help")
class HelpCommand : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
    )
    fun SlashSender.help() {
        val embed = EmbedBuilder()

        embed.setAuthor("Help Menu:")
        embed.appendDescription(
            """  
                `1.` /play <identifier> - Play a song.
                `2.` /clear - Clear the queue.
                `3.` /skip - Skip the currently playing song.
                `4.` /stop - Stop the audio and clear the queue.
                `5.` /remove <amount> - Remove songs from the queue.
                `6.` /remove-dupes - Remove all duplicates from the queue.
                `7.` /shuffle - Shuffle the queue.
                
                `8.` /join - Make the bot join your voice channel.
                `9.` /leave - Make the bot leave the voice channel.
                `10.` /pause - Pause the audio.
                `11.` /resume - Resume the audio.
                
                `12.` /loop - Toggle looping for the currently playing song.
                `13.` /replay - Toggle replay for the currently playing song.
                `14.` /radio - Toggle the radio.
                `15.` /volume - Turn the volume up or down.
                
                `16.` /queue - List all the songs that are currently queued.
                `17.` /np - List the currently playing song.
                `18.` /grab - Get a DM listing the currently playing song.
                
                `19.` /forward <seconds> [minutes] [hours] - Forward the currently playing song by a certain amount of time.
                `20`. /rewind <seconds> [minutes] [hours] - Rewind the currently playing song by a certain amount of time.
                `21.` /seek <seconds> [minutes] [hours] - Seek to a certain moment in the currently playing song.
            """.trimIndent()
        )

        embed
            .setFooter("Requested by: ${member?.effectiveName}")
            .setTimestamp(Instant.now())
            .setColor(AlphaMusic.EMBED_COLOR)

        reply(embed.build()).queue()
    }
}