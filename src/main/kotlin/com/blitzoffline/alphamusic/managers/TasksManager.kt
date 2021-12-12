package com.blitzoffline.alphamusic.managers

import com.blitzoffline.alphamusic.audio.MusicManager
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import net.dv8tion.jda.api.entities.Guild

class TasksManager {
    private val clearTasks = hashMapOf<String, TimerTask>()
    private val leaveTasks = hashMapOf<String, TimerTask>()

    fun getLeaveTasks(): HashMap<String, TimerTask> {
        return leaveTasks
    }

    @Synchronized fun addLeaveTask(guild: Guild) {
        if (leaveTasks[guild.id] != null) {
            return
        }

        leaveTasks[guild.id] = Timer().schedule(300000) {
            guild.audioManager.closeAudioConnection()
            removeLeaveTask(guild.id)
        }
    }

    @Synchronized fun removeLeaveTask(guildId: String) {
        leaveTasks[guildId]?.cancel()
        leaveTasks.remove(guildId)
    }

    fun getClearTasks() : HashMap<String, TimerTask> {
        return clearTasks
    }

    @Synchronized fun addClearTask(musicManager: MusicManager) {
        if (clearTasks[musicManager.guild.id] != null) {
            return
        }

        clearTasks[musicManager.guild.id] = Timer().schedule(300000) {
            musicManager.audioHandler.queue.clear()
            musicManager.audioHandler.nextTrack()
            removeClearTask(musicManager.guild.id)
        }
    }

    @Synchronized fun removeClearTask(guildId: String) {
        clearTasks[guildId]?.cancel()
        clearTasks.remove(guildId)
    }
}