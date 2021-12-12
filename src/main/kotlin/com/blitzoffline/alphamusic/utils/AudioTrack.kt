package com.blitzoffline.alphamusic.utils

import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun AudioTrack?.asEmbed(icon: String? = null): MessageEmbed? {
    return if (this == null)
        null
    else
        EmbedBuilder()
            .setAuthor("Now Playing ♪", null, icon)
            .setTitle(info.title, info.uri)
            .setThumbnail(info.artworkUrl)
            .setDescription("""
                    `${progressBar(position, duration)}`
                    
                    `${formatHMSDouble(Duration.ofMillis(position), Duration.ofMillis(duration))}`
                    
                    `Requested by:` ${getUserData(TrackMetadata::class.java).data.name}
                """.trimIndent())
            .build()
}