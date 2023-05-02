package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import com.blitzoffline.alphamusic.votes.VoteType
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.Permission

@Command("leave")
@Description("Make the bot leave the voice channel!")
class LeaveCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashCommandSender.leave() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)
        val voteManager = musicManager.voteManager.getVoteManager(VoteType.LEAVE)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            guild.audioManager.closeAudioConnection()
            return event.terminate(reason = "Left the voice channel!")
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return

        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            guild.audioManager.closeAudioConnection()
            return event.terminate(reason = "Left the voice channel!")
        }

        if (voteManager == null) {
            return event.terminate(reason = "Could not process your vote!", ephemeral = true)
        }

        if (voteManager.votes.contains(member.id)) {
            return event.terminate(reason = "You have already voted!", ephemeral = true)
        }

        val required = voteManager.getRequiredVotes(participants.size)

        if (voteManager.votes.size + 1 == required) {
            voteManager.votes.clear()
            guild.audioManager.closeAudioConnection()
            return event.terminate(reason = "Left the voice channel!")
        }

        voteManager.votes.add(member.id)
        return event.terminate(reason = "Added vote for the bot to leave. Total votes: ${voteManager.votes.size}/$required!")

    }
}