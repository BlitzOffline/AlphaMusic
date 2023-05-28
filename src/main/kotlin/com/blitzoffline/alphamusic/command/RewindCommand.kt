package com.blitzoffline.alphamusic.command

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMS
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotations.*
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import java.time.Duration
import net.dv8tion.jda.api.Permission

@Command("rewind")
@Description("Rewind the currently playing song by a certain amount of time!")
class RewindCommand(private val bot: AlphaMusic) {
    @Command
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
    )
    fun SlashCommandSender.rewind(
        @Description("Amount of seconds to rewind by!") seconds: Int,
        @Description("Amount of minutes to rewind by!") minutes: Int,
        @Description("Amount of hours to rewind by!") @Optional hours: Int?
    ) {
        if (minutes != null && minutes < 0) {
            return event.terminate(reason = "Minutes can not be < 0!", ephemeral = true)
        }

        if (hours != null && hours < 0) {
            return event.terminate(reason = "Hours can not be < 0!", ephemeral = true)
        }

        if (seconds < 0) {
            return event.terminate(reason = "Seconds can not be < 0!", ephemeral = true)
        }

        if (seconds == 0 && ((minutes == null || minutes == 0) && (hours == null || hours == 0))) {
            return event.terminate(reason = "Seconds can not be 0 when hours and minutes are 0!", ephemeral = true)
        }

        val guild = guild ?: return
        val member = member ?: return
        val musicManager = bot.getMusicManager(guild)

        val playing = musicManager.player.playingTrack
            ?: return event.terminate(reason = "There is no song playing currently!", ephemeral = true)

        val meta = playing.userData as TrackMetadata
        val channel = guild.selfMember.voiceState?.channel ?: return
        if (!member.hasPermission(Permission.ADMINISTRATOR) && meta.data.id != member.id && channel.members.size > 2) {
            return event.terminate(
                reason = "Only the requester of the song can do this. Requester: ${meta.data.name}#${meta.data.discriminator}",
                ephemeral = true
            )
        }

        var total = seconds.toLong()

        if (minutes != null) {
            total += minutes * 60
        }

        if (hours != null) {
            total += hours * 3600
        }

        total *= 1000

        playing.position = playing.position - total
        event.terminate(reason = "Rewound song to: ${formatHMS(Duration.ofMillis(playing.position))}!")
    }
}