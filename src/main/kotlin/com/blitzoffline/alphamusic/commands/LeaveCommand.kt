package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("leave")
@Description("Make the bot leave the voice channel!")
class LeaveCommand : BaseCommand() {
    @Default
    fun SlashSender.leave() {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        val guild = guild ?: return

        guild.audioManager.closeAudioConnection()
        event.terminate("Left the voice channel!")
    }
}