package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender

@Command("join")
@Description("Make the bot join your voice channel!")
class JoinCommand {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_already_in_vc", invert = true),
        Requirement("member_in_vc", messageKey = "member_not_in_vc"),
    )
    fun SlashCommandSender.join() {
        val guild = guild ?: return
        val member = member ?: return
        val memberVC = member.voiceState?.channel ?: return

        if (kotlin.runCatching { guild.audioManager.openAudioConnection(memberVC) }.isFailure) {
            return event.terminate(reason = "Could not connect to your voice channel!", ephemeral = true)
        }

        event.terminate(reason = "Joined your voice channel successfully!")
    }
}