package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild

class AudioHandler(private val bot: AlphaMusic, private val player: AudioPlayer, private val guild: Guild) : AudioEventAdapter(), AudioSendHandler {
    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame()

    val queue = ArrayBlockingQueue<AudioTrack>(250)

    var radio = false
    var replay = false
    var loop = false

    fun queue(track: AudioTrack): Boolean {
        if (player.playingTrack == null) {
            player.playTrack(track)
            bot.taskManager.removeLeaveTask(guild.id)
            return true
        }
        return queue.offer(track)
    }

    fun nextTrack(previous: AudioTrack? = null) {
        if (previous != null && radio && queue.size == 0) {
            val identifier = "https://www.youtube.com/watch?v=${previous.identifier}&list=RD${previous.identifier}"
            return bot.trackService.loadTrack(identifier, guild, event = null, isRadio = true)
        }

        if (player.startTrack(queue.poll(), false)) {
            return bot.taskManager.removeLeaveTask(guild.id)
        }
        bot.taskManager.addLeaveTask(guild)
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

    fun clear(): Int {
        val amount = queue.size
        queue.clear()
        return amount
    }

    fun remainingCapacity(): Int {
        return queue.remainingCapacity()
    }

    fun size(): Int {
        return queue.size
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

        nextTrack(track)
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