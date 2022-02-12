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

class AudioHandler(private val bot: AlphaMusic, private val player: AudioPlayer, private val guildId: String) : AudioEventAdapter(), AudioSendHandler {
    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame()

    /**
     * The queue. It can hold up to 250 [AudioTrack]s.
     */
    private val queue = ArrayBlockingQueue<AudioTrack>(250)

    /**
     * If enabled, there will be an attempt to generate a new queue
     * based on the last played [AudioTrack] when there is no [AudioTrack]
     * left in the queue.
     */
    var radio = false

    /**
     * Marks the currently playing [AudioTrack] to be replayed when it ends.
     */
    var replay = false

    /**
     * Marks the currently playing [AudioTrack] to be looped indefinitely.
     */
    var loop = false

    /**
     * Attempts to play a new [AudioTrack] from the queue and
     * if there are no [AudioTrack]s left in the queue but the Radio Mode
     * is enabled it will attempt to generate a new queue and play it.
     *
     * @param previous the track that was played previously if there is one.
     * This is used only when Radio Mode is enabled.
     */
    private fun nextTrack(previous: AudioTrack? = null) {
        if (previous != null && radio && queue.size == 0) {
            val identifier = "https://www.youtube.com/watch?v=${previous.identifier}&list=RD${previous.identifier}"
            return bot.trackService.loadTrack(identifier, bot.jda.guilds.first { it.id == guildId }, event = null, isRadio = radio)
        }

        if (player.startTrack(queue.poll(), false)) {
            return bot.taskManager.removeLeaveTask(guildId)
        }
        bot.taskManager.addLeaveTask(bot.jda.guilds.first { it.id == guildId })
    }

    /**
     * Queue an [AudioTrack].
     *
     * If there is no playing [AudioTrack] currently, there will
     * be an attempt to play the provided [AudioTrack].
     *
     * @param track the [AudioTrack] that is going to be queued.
     * @return true if the track was successfully played or added to the queue, false otherwise.
     */
    fun queue(track: AudioTrack): Boolean {
        if (player.playingTrack == null) {
            player.playTrack(track)
            bot.taskManager.removeLeaveTask(guildId)
            return true
        }
        return queue.offer(track)
    }

    /**
     * Skips the currently playing [AudioTrack] and attempts to play
     * a new track from the queue if there is one. If there is no [AudioTrack]
     * in the queue and the Radio Mode is enabled it will attempt to
     * generate a new queue and play it.
     */
    fun skip() {
        nextTrack(player.playingTrack)
    }

    /**
     * Shuffles the queue.
     */
    fun shuffle() {
        val shuffled = queue.shuffled()
        queue.clear()
        queue.addAll(shuffled)
    }

    /**
     * Removes duplicates from the queue.
     *
     * Duplicates are established based on their identifier.
     * It does not count the currently playing [AudioTrack].
     *
     * @return the number of duplicates that were removed.
     */
    fun removeDupes(): Int {
        val distinct = queue.distinctBy { it.info.identifier }
        val removed = queue.size - distinct.size

        queue.clear()
        queue.addAll(distinct)

        return removed
    }

    /**
     * Removes the first track from the queue if there is one.
     */
    fun removeNext() {
        queue.poll()
    }

    /**
     * Clears the queue.
     *
     * @return the number of [AudioTrack]s that were in the queue
     * before it was cleared.
     */
    fun clear(): Int {
        val amount = queue.size
        queue.clear()
        return amount
    }

    /**
     * @return the remaining capacity of the queue.
     */
    fun remainingCapacity(): Int {
        return queue.remainingCapacity()
    }

    /**
     * @return the amount of [AudioTrack]s currently in the queue.
     */
    fun size(): Int {
        return queue.size
    }

    /**
     * @return the current queue as a [List].
     */
    fun queue(): List<AudioTrack> {
        return ArrayList(queue)
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