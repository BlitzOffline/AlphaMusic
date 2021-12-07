package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("join")
@Description("Make the bot join your voice channel!")
class JoinCommand : BaseCommand() {
    @Default
    fun SlashSender.join() {
        if (event.guild?.selfMember?.voiceState?.channel != null) {
            return event.terminate("The bot is already in a voice channel!")
        }

        if (!process(join = true)) {
            return
        }

        event.terminate("Joined your channel successfully!")
    }
}