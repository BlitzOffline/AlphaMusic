package com.blitzoffline.alphamusic.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue
import net.dv8tion.jda.api.audio.AudioSendHandler

class AudioHandler(private val player: AudioPlayer) : AudioEventAdapter(), AudioSendHandler {
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
        val distinct = queue.toList().distinctBy { it.info.identifier }
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
}