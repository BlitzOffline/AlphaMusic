package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.util.concurrent.ArrayBlockingQueue

@Command("remove")
@Description("Remove a song from the queue!")
class RemoveCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    // todo: change index to amount to remove the need of cloning the queue.
    fun SlashSender.remove(@Description("Index of song you want to remove. Starts from 1!") index: Int) {
        if (index <= 0) {
            return event.terminate("The index needs to be greater than 0!")
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The queue is empty!")
        }

        val size = musicManager.audioHandler.queue.size
        if (index > size) {
            return event.terminate("There are only $size songs queued!")
        }

        var count = 0
        val tempQueue = ArrayBlockingQueue<AudioTrack>(size - 1)
        for (track in musicManager.audioHandler.queue) {
            if (count + 1 == index) {
                count++
                continue
            }

            tempQueue.add(track)
            count++
        }

        musicManager.audioHandler.queue.clear()
        musicManager.audioHandler.queue.addAll(tempQueue)
        tempQueue.clear()

        event.terminate("Removed song from queue!")
    }
}