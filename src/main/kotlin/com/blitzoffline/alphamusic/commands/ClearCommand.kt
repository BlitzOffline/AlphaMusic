package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import com.blitzoffline.alphamusic.votes.VoteType
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.Permission

@Command("clear")
@Description("Clear the queue!")
class ClearCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.clear() {
        val guild = guild ?: return
        val member = member ?: return

        val musicManager = bot.getMusicManager(guild)
        if (musicManager.audioHandler.queue.isEmpty()) {
            return event.terminate("The queue is already empty!")
        }

        val voteManager = musicManager.voteHandler.getVoteManager(VoteType.CLEAR)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.clear()
            return event.terminate("Cleared the queue!")
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return

        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.clear()
            return event.terminate("Cleared the queue!")
        }

        if (voteManager == null) {
            return event.terminate("Could not process your vote!")
        }

        if (voteManager.votes.contains(member.id)) {
            return event.terminate("You have already voted!")
        }

        val required = voteManager.getRequiredVotes(participants.size)

        if (voteManager.votes.size + 1 == required) {
            voteManager.votes.clear()
            musicManager.audioHandler.clear()
            return event.terminate("Cleared the queue!")
        }

        voteManager.votes.add(member.id)
        return event.terminate("Added vote for the queue to be cleared. Total votes: ${voteManager.votes.size}/$required!")
    }
}