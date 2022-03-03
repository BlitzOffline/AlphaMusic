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

@Command("stop")
@Description("Stop the audio and clear the queue!")
class StopCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.stop() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null && musicManager.audioHandler.size() == 0) {
            return event.terminate("The bot is not playing any audio!")
        }

        val voteManager = musicManager.voteManager.getVoteManager(VoteType.CLEAR)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.clear()
            musicManager.player.stopTrack()
            bot.taskManager.addLeaveTask(bot.jda, guild.id)
            return event.terminate("Stopped the audio!")
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return

        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.clear()
            musicManager.player.stopTrack()
            bot.taskManager.addLeaveTask(bot.jda, guild.id)
            return event.terminate("Stopped the audio!")
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
            musicManager.player.stopTrack()
            bot.taskManager.addLeaveTask(bot.jda, guild.id)
            return event.terminate("Stopped the audio!")
        }

        voteManager.votes.add(member.id)
        return event.terminate("Added vote for the bot to be stopped. Total votes: ${voteManager.votes.size}/$required!")
    }
}