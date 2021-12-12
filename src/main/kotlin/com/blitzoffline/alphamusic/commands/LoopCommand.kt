package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("loop")
@Description("Toggle looping for playing song!")
class LoopCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.loop() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)
        musicManager.audioHandler.loop = !musicManager.audioHandler.loop

        if (musicManager.audioHandler.loop) {
            return event.terminate("The song will now be looped!")
        }

        event.terminate("The song will no longer be looped!")
    }
}