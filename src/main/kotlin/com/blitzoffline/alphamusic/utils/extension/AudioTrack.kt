package com.blitzoffline.alphamusic.utils.extension

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.track.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMSDouble
import com.blitzoffline.alphamusic.utils.progressBar
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.Duration

fun AudioTrack?.asEmbedNullable(author: String = "Now Playing ♪", icon: String? = null, showTimestamp: Boolean = false): MessageEmbed? {
    if (this == null) {
        return null
    }

    return this.asEmbed(author, icon, showTimestamp)
}

fun AudioTrack.asEmbed(author: String = "Now Playing ♪", icon: String? = null, showTimestamp: Boolean = false): MessageEmbed {
    val embed = EmbedBuilder()
        .setAuthor(author, null, icon)
        .setTitle(info.title, info.uri)
        .setThumbnail(info.artworkUrl)
        .setColor(AlphaMusic.EMBED_COLOR)

    if (showTimestamp) {
        embed.appendDescription("""
                    `${progressBar(position, duration)}`
                    
                    `${formatHMSDouble(Duration.ofMillis(position), Duration.ofMillis(duration))}`
                    
                    `Requested by:` ${getUserData(TrackMetadata::class.java).data.name}
                """.trimIndent())
    }

    return embed.build()
}