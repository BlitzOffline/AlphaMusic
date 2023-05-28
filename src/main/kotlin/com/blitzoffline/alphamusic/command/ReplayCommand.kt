package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.terminate
import com.blitzoffline.alphamusic.vote.VoteType
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Description
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.Permission

@Command("replay")
@Description("Toggle replay for the currently playing song!")
class ReplayCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashCommandSender.replay() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null) {
            return event.terminate(reason = "There is no song currently playing to be replayed!", ephemeral = true)
        }

        val voteManager = musicManager.voteManager.getVoteManager(VoteType.REPLAY)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.replay = !musicManager.audioHandler.replay
            return if (musicManager.audioHandler.replay) {
                event.terminate(reason = "Currently playing song marked to be replayed!")
            } else {
                event.terminate(reason = "Currently playing song marked to no longer be replayed!")
            }
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return
        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.replay = !musicManager.audioHandler.replay
            return if (musicManager.audioHandler.replay) {
                event.terminate(reason = "Currently playing song marked to be replayed!")
            } else {
                event.terminate(reason = "Currently playing song marked to no longer be replayed!")
            }
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
            musicManager.audioHandler.replay = !musicManager.audioHandler.replay
            return if (musicManager.audioHandler.replay) {
                event.terminate(reason = "Currently playing song marked to be replayed!")
            } else {
                event.terminate(reason = "Currently playing song marked to no longer be replayed!")
            }
        }

        voteManager.votes.add(member.id)
        event.terminate(reason = "Added vote for the current song to be replayed. Total votes: ${voteManager.votes.size}/$required!")
    }
}