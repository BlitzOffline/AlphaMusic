package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.utils.formatHMS
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.Requirement
import dev.triumphteam.cmd.core.annotation.Requirements
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Duration
import net.dv8tion.jda.api.Permission

@Command("forward")
@Description("Forward the current song by a certain amount of time!")
class ForwardCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        Requirement("same_channel_or_admin", messageKey = "not_same_channel_or_admin"),
    )
    fun SlashSender.forward(
        @Description("Amount of seconds to forward by!") seconds: Int,
        @Description("Amount of minutes to forward by!") @Optional minutes: Int?,
        @Description("Amount of hours to forward by!") @Optional hours: Int?
    ) {
        val member = event.member ?: return

        if (minutes != null && minutes < 0) {
            return event.terminate("Minutes can not be < 0!")
        }

        if (hours != null && hours < 0) {
            return event.terminate("Hours can not be < 0!")
        }

        if (seconds < 0) {
            return event.terminate("Seconds can not be < 0!")
        }

        if (seconds == 0 && ((minutes == null || minutes == 0) && (hours == null || hours == 0))) {
            return event.terminate("Seconds can not be 0 when hours and minutes are 0!")
        }

        val guild = guild ?: return
        val musicManager = bot.getMusicManager(guild)

        val playing = musicManager.player.playingTrack
            ?: return event.terminate("There is no song playing currently!")

        val trackData = playing.userData as TrackMetadata
        if (trackData.data.id != member.id && !member.hasPermission(Permission.ADMINISTRATOR)) {
            return event.terminate("Only admins and the user that played the song can forward it!")
        }

        var total = seconds.toLong()

        if (minutes != null) {
            total += minutes * 60
        }

        if (hours != null) {
            total += hours * 3600
        }

        total *= 1000

        if (playing.position + total > playing.duration) {
            return event.terminate("The song is not that long! Song Duration: ${formatHMS(Duration.ofMillis(playing.duration))} | " +
                    "Current timestamp: ${formatHMS(Duration.ofMillis(playing.position))}")
        }

        playing.position = playing.position + total
        event.terminate("Forwarded song to: ${formatHMS(Duration.ofMillis(playing.position))}!")
    }
}