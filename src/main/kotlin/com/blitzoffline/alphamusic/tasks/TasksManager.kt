package com.blitzoffline.alphamusic.tasks

import java.util.TimerTask

class TasksManager {
    private var clearTasks = hashMapOf<String, TimerTask>()
    private var leaveTasks = hashMapOf<String, TimerTask>()

    fun addLeaveTask(guildId: String, task: TimerTask) {
        if (leaveTasks[guildId] != null) {
            removeLeaveTask(guildId)
        }

        leaveTasks[guildId] = task
    }

    fun removeLeaveTask(guildId: String) {
        leaveTasks[guildId]?.cancel()
        leaveTasks.remove(guildId)
    }

    fun addClearTask(guildId: String, task: TimerTask) {
        if (clearTasks[guildId] != null) {
            removeClearTask(guildId)
        }

        clearTasks[guildId] = task
    }

    fun removeClearTask(guildId: String) {
        clearTasks[guildId]?.cancel()
        clearTasks.remove(guildId)
    }
}