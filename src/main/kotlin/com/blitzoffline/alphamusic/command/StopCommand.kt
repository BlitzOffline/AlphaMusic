package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import com.blitzoffline.alphamusic.vote.VoteType
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.Permission

@Command("stop")
@Description("Stop the audio and clear the queue!")
class StopCommand(private val guildManagersHolder: GuildManagersHolder) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashCommandSender.stop() {
        val guild = guild ?: return
        val member = member ?: return
        val guildManager = guildManagersHolder.getGuildManager(guild)

        if (guildManager.audioPlayer.playingTrack == null && guildManager.audioHandler.size() == 0) {
            return event.terminate(reason = "The bot is not playing any audio!", ephemeral = true)
        }

        val voteManager = guildManager.voteManager.getVoteManager(VoteType.CLEAR)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            guildManager.audioHandler.clear()
            guildManager.audioPlayer.stopTrack()
            guildManager.addLeaveTask()
            return event.terminate(reason = "Stopped the audio!")
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return

        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            guildManager.audioHandler.clear()
            guildManager.audioPlayer.stopTrack()
            guildManager.addLeaveTask()
            return event.terminate(reason = "Stopped the audio!")
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
            guildManager.audioHandler.clear()
            guildManager.audioPlayer.stopTrack()
            guildManager.addLeaveTask()
            return event.terminate(reason = "Stopped the audio!")
        }

        voteManager.votes.add(member.id)
        return event.terminate(reason = "Added vote for the bot to be stopped. Total votes: ${voteManager.votes.size}/$required!")
    }
}