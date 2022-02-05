package com.blitzoffline.alphamusic.utils

import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun AudioTrack?.asEmbed(author: String = "Now Playing ♪", icon: String? = null, showTimestamp: Boolean = false): MessageEmbed? {
    if (this == null) {
        return null
    }

    val embed = EmbedBuilder()
        .setAuthor(author, null, icon)
        .setTitle(info.title, info.uri)
        .setThumbnail(info.artworkUrl)

    if (showTimestamp) {
        embed.appendDescription("""
                    `${progressBar(position, duration)}`
                    
                    `${formatHMSDouble(Duration.ofMillis(position), Duration.ofMillis(duration))}`
                    
                    `Requested by:` ${getUserData(TrackMetadata::class.java).data.name}
                """.trimIndent())
    }

    return embed.build()
}