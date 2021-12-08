package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.utils.formatHMSDouble
import com.blitzoffline.alphamusic.utils.progressBar
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import java.nio.Buffer
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.ArrayBlockingQueue
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.MessageEmbed

class AudioHandler(private val player: AudioPlayer, private val jda: JDA) : AudioEventAdapter(), AudioSendHandler {
    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame()

    val queue = ArrayBlockingQueue<AudioTrack>(250)

    var replay = false
    var loop = false

    fun queue(track: AudioTrack): Boolean {
        if (player.playingTrack == null) {
            player.playTrack(track)
            return true
        }
        return queue.offer(track)
    }

    fun nextTrack() {
        player.startTrack(queue.poll(), false)
    }

    fun shuffle() {
        val shuffled = queue.shuffled()
        queue.clear()
        queue.addAll(shuffled)
    }

    fun removeDupes(): Int {
        val distinct = queue.distinctBy { it.info.identifier }
        val removed = queue.size - distinct.size

        queue.clear()
        queue.addAll(distinct)

        return removed
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (!endReason.mayStartNext) {
            return
        }

        if (loop) {
            return player.playTrack(track.makeClone())
        }

        if (replay) {
            replay = false
            return player.playTrack(track.makeClone())
        }

        nextTrack()
    }

    override fun canProvide(): Boolean {
        return player.provide(frame)
    }

    override fun provide20MsAudio(): ByteBuffer {
        (buffer as Buffer).flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }

    init {
        frame.setBuffer(buffer)
    }

    fun nowPlayingAsEmbed(): MessageEmbed? {
        val playing = player.playingTrack ?: return null
        return EmbedBuilder()
            .setAuthor("Now Playing ♪", null, jda.selfUser.avatarUrl)
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