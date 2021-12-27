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

@Command("loop")
@Description("Toggle looping for playing song!")
class LoopCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("IN_GUILD", messageKey = "IN_GUILD"),
        Requirement("BOT_IS_IN_VC", messageKey = "BOT_IS_IN_VC"),
        Requirement("SAME_CHANNEL_OR_ADMIN", messageKey = "SAME_CHANNEL_OR_ADMIN")
    )
    fun SlashSender.loop() {
        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        if (member.permissions.contains(Permission.ADMINISTRATOR)) {
            musicManager.audioHandler.loop = !musicManager.audioHandler.loop
            return if (musicManager.audioHandler.loop) {
                event.terminate("The song will now be looped!")
            } else {
                event.terminate("The song will no longer be looped!")
            }
        }

        val participants = guild.selfMember.voiceState?.channel?.members ?: return

        if (participants.size <= 2) {
            musicManager.audioHandler.loop = !musicManager.audioHandler.loop
            return if (musicManager.audioHandler.loop) {
                event.terminate("The song will now be looped!")
            } else {
                event.terminate("The song will no longer be looped!")
            }
        }

        val voteManager = musicManager.votesManager.getVoteManager(VoteType.LOOP)
            ?: return event.terminate("Could not process your vote!")

        if (voteManager.votes.contains(member.id)) {
            return event.terminate("You have already voted!")
        }

        val required = voteManager.getRequiredVotes(participants.size)

        if (voteManager.votes.size + 1 == required) {
            voteManager.votes.clear()
            musicManager.audioHandler.loop = !musicManager.audioHandler.loop
            return if (musicManager.audioHandler.loop) {
                event.terminate("The song will now be looped!")
            } else {
                event.terminate("The song will no longer be looped!")
            }
        }

        voteManager.votes.add(member.id)
        return event.terminate("Added vote for the loop to be toggled. Total votes: ${voteManager.votes.size}/$required!")
    }
}