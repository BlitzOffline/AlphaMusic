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

@Command("replay")
@Description("Mark a song to be replayed!")
class ReplayCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    // todo: make this command a toggle.
    fun SlashSender.replay() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        if (musicManager.player.playingTrack == null) {
            return event.terminate("There is no song currently playing to be replayed!")
        }

        if (musicManager.audioHandler.replay) {
            return event.terminate("Currently playing song is already marked to be replayed!")
        }

        val voteManager = musicManager.voteHandler.getVoteManager(VoteType.REPLAY)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.replay = true
            return event.terminate("Currently playing song marked to be replayed!")
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return
        if (participants.size <= 2) {
            voteManager?.votes?.clear()
            musicManager.audioHandler.replay = true
            return event.terminate("Currently playing song marked to be replayed!")
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
            musicManager.audioHandler.replay = true
            return event.terminate("Currently playing song marked to be replayed!")
        }

        voteManager.votes.add(member.id)
        event.terminate("Added vote for the current song to be replayed. Total votes: ${voteManager.votes.size}/$required!")
    }
}