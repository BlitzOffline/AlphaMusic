package com.blitzoffline.alphamusic.manager

import com.blitzoffline.alphamusic.handler.GuildAudioHandler
import com.blitzoffline.alphamusic.holder.CachedGuildHolder
import com.blitzoffline.alphamusic.track.TrackLoader
import com.blitzoffline.alphamusic.vote.VoteManager
import net.dv8tion.jda.api.JDA
import java.util.*
import kotlin.concurrent.schedule

class GuildManager(
    val guildId: String,
    val jda: JDA,
    val trackLoader: TrackLoader,
    val audioPlayerManager: AudioPlayerManager,
    val guildHolder: CachedGuildHolder,
) {
    val voteManager = VoteManager()
    val audioPlayer = audioPlayerManager.createPlayer()
    val audioHandler = GuildAudioHandler(guildId, jda, guildHolder, trackLoader, audioPlayer, this)

    init {
        audioPlayer.addListener(audioHandler)
    }

    private var clearTask: TimerTask? = null
    private var leaveTask: TimerTask? = null

    @Synchronized fun addLeaveTask(delay: Long = 300000) {
        if (leaveTask != null) {
            return
        }

        leaveTask = Timer().schedule(delay) {
            val guild = jda.guildCache.firstOrNull { it.id == guildId } ?: return@schedule
            guild.audioManager.closeAudioConnection()
            removeLeaveTask()
        }
    }

    @Synchronized fun removeLeaveTask() {
        leaveTask?.cancel()
        leaveTask = null
    }

    @Synchronized fun addClearTask(delay: Long = 300000) {
        if (clearTask != null) {
            return
        }

        clearTask = Timer().schedule(delay) {
            audioHandler.clear()
            audioHandler.skip()
            removeClearTask()
        }
    }

    @Synchronized fun removeClearTask() {
        clearTask?.cancel()
        clearTask = null
    }
}