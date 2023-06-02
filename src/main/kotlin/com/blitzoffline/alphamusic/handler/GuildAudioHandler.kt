package com.blitzoffline.alphamusic.handler

import com.blitzoffline.alphamusic.holder.CachedGuild
import com.blitzoffline.alphamusic.holder.CachedGuildHolder
import com.blitzoffline.alphamusic.manager.GuildManager
import com.blitzoffline.alphamusic.track.TrackLoader
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue

/**
 * Handles the audio for a [Guild].
 * @param jda the [JDA] instance.
 * @param guildHolder a [CachedGuildHolder] that holds a [CachedGuild] that represents the [Guild] this [GuildAudioHandler] is for.
 * @param trackLoader the [TrackLoader] instance.
 * @param player the [AudioPlayer] instance.
 * @param guildManager the [GuildManager] instance.
 */
class GuildAudioHandler(
    private val guildId: String,
    private val jda: JDA,
    private val guildHolder: CachedGuildHolder,
    private val trackLoader: TrackLoader,
    private val player: AudioPlayer,
    private val guildManager: GuildManager
) : AudioEventAdapter(), AudioSendHandler {

    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame()

    /**
     * The queue. It can hold up to 250 [AudioTrack]s.
     */
    private val queue = ArrayBlockingQueue<AudioTrack>(250)

    /**
     * Attempts to play a new [AudioTrack] from the queue and
     * if there are no [AudioTrack]s left in the queue but the Radio Mode
     * is enabled it will attempt to generate a new queue and play it.
     *
     * @param previous the track that was played previously if there is one.
     * This is used only when Radio Mode is enabled.
     */
    private fun nextTrack(previous: AudioTrack? = null) {
        if (previous != null && guildHolder.radio(guildId) && queue.size == 0) {
            val jdaGuild = jda.guilds.firstOrNull { it.id == guildId }
            if (jdaGuild == null) {
                guildManager.addLeaveTask()
                return
            }

            trackLoader.loadTrack(
                generateRadioLink(previous),
                guildManager,
                event = null,
            )
            return
        }

        if (player.startTrack(queue.poll(), false)) {
            return guildManager.removeLeaveTask()
        }
        guildManager.addLeaveTask()
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
            guildManager.removeLeaveTask()
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
        val currentTrack = player.playingTrack
        player.stopTrack()
        nextTrack(currentTrack)
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

    /**
     * Toggles the radio on or off.
     */
    fun toggleRadio(): Boolean {
        val oldValue = guildHolder.radio(guildId)
        guildHolder.setRadio(guildId, !oldValue)
        return !oldValue
    }

    fun generateRadioLink(previous: AudioTrack): String {
        val builder = StringBuilder()
        builder.append("https://www.youtube.com/watch?v=")
        // seed
        builder.append(previous.identifier)
        // last played
        builder.append("&list=RD${previous.identifier}")

        return builder.toString()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (!endReason.mayStartNext) {
            return
        }

        if (guildHolder.loop(guildId)) {
            return player.playTrack(track.makeClone())
        }

        if (guildHolder.replay(guildId)) {
            guildHolder.setReplay(guildId, false)
            return player.playTrack(track.makeClone())
        }

        nextTrack(track)
    }

    override fun onPlayerPause(player: AudioPlayer?) {
        guildManager.addLeaveTask()
    }

    override fun onPlayerResume(player: AudioPlayer?) {
        guildManager.removeLeaveTask()
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