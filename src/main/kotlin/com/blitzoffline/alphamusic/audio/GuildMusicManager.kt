package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.formatHMSDouble
import com.blitzoffline.alphamusic.utils.progressBar
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

class GuildMusicManager(private val bot: AlphaMusic) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(player)

    init {
        player.addListener(audioHandler)
    }

    fun playing(): MessageEmbed? {
        val playing = player.playingTrack ?: return null
        return EmbedBuilder()
            .setAuthor("Now Playing ♪", null, bot.jda.selfUser.avatarUrl)
            .setTitle(playing.info.title, playing.info.uri)
            .setThumbnail(playing.info.artworkUrl)
            .setDescription("""
                    `${progressBar(playing.position, playing.duration)}`
                    
                    `${formatHMSDouble(Duration.ofMillis(playing.position), Duration.ofMillis(playing.duration))}`
                    
                    `Requested by:` ${playing.getUserData(TrackMetadata::class.java).data.name}
                """.trimIndent())
            .build()
    }
}