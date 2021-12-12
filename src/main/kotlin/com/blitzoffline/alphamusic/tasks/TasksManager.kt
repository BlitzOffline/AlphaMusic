package com.blitzoffline.alphamusic.tasks

import com.blitzoffline.alphamusic.audio.MusicManager
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import net.dv8tion.jda.api.entities.Guild

class TasksManager {
    private var clearTasks = hashMapOf<String, TimerTask>()
    private var leaveTasks = hashMapOf<String, TimerTask>()

    fun addLeaveTask(guild: Guild) {
        if (leaveTasks[guild.id] != null) {
            removeLeaveTask(guild.id)
        }

        leaveTasks[guild.id] = Timer().schedule(300000) {
            guild.audioManager.closeAudioConnection()
            removeLeaveTask(guild.id)
        }
    }

    fun removeLeaveTask(guildId: String) {
        leaveTasks[guildId]?.cancel()
        leaveTasks.remove(guildId)
    }

    fun addClearTask(musicManager: MusicManager) {
        if (clearTasks[musicManager.guild.id] != null) {
            removeClearTask(musicManager.guild.id)
        }

        clearTasks[musicManager.guild.id] = Timer().schedule(300000) {
            musicManager.audioHandler.queue.clear()
            musicManager.audioHandler.nextTrack()
            removeClearTask(musicManager.guild.id)
        }
    }

    fun removeClearTask(guildId: String) {
        clearTasks[guildId]?.cancel()
        clearTasks.remove(guildId)
    }
}