package com.blitzoffline.alphamusic.tasks

import com.blitzoffline.alphamusic.audio.GuildMusicManager
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import net.dv8tion.jda.api.JDA

class TaskManager {
    private val clearTasks = hashMapOf<String, TimerTask>()
    private val leaveTasks = hashMapOf<String, TimerTask>()

    fun getLeaveTasks(): HashMap<String, TimerTask> {
        return leaveTasks
    }

    @Synchronized fun addLeaveTask(jda: JDA, guildId: String, delay: Long = 300000) {
        if (leaveTasks[guildId] != null) {
            return
        }

        leaveTasks[guildId] = Timer().schedule(delay) {
            val guild = jda.guildCache.firstOrNull { it.id == guildId } ?: return@schedule
            guild.audioManager.closeAudioConnection()
            removeLeaveTask(guildId)
        }
    }

    @Synchronized fun removeLeaveTask(guildId: String) {
        leaveTasks[guildId]?.cancel()
        leaveTasks.remove(guildId)
    }

    fun getClearTasks() : HashMap<String, TimerTask> {
        return clearTasks
    }

    @Synchronized fun addClearTask(musicManager: GuildMusicManager, delay: Long = 300000) {
        if (clearTasks[musicManager.guildId] != null) {
            return
        }

        clearTasks[musicManager.guildId] = Timer().schedule(delay) {
            musicManager.audioHandler.clear()
            musicManager.audioHandler.skip()
            removeClearTask(musicManager.guildId)
        }
    }

    @Synchronized fun removeClearTask(guildId: String) {
        clearTasks[guildId]?.cancel()
        clearTasks.remove(guildId)
    }
}