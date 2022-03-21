package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("join")
@Description("Make the bot join your voice channel!")
class JoinCommand : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_already_in_vc", invert = true),
        Requirement("member_in_vc", messageKey = "member_not_in_vc"),
    )
    fun SlashSender.join() {
        val guild = guild ?: return
        val member = member ?: return
        val memberVC = member.voiceState?.channel ?: return

        if (kotlin.runCatching { guild.audioManager.openAudioConnection(memberVC) }.isFailure) {
            return event.terminate("Could not connect to your voice channel!")
        }

        event.terminate("Joined your voice channel successfully!")
    }
}