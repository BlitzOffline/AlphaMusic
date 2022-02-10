package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
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

@Command("seek")
@Description("Seek to a certain moment in the song!")
class SeekCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
        // todo: skip this check if there's only the command caller with the bot in vc
        Requirement("requester_or_admin", messageKey = "not_requester_or_admin"),
    )
    fun SlashSender.seek(
        @Description("Amount of seconds to seek!") seconds: Int,
        @Description("Amount of minutes to seek!") @Optional minutes: Int?,
        @Description("Amount of hours to seek!") @Optional hours: Int?
    ) {
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

        var total = seconds.toLong()

        if (minutes != null) {
            total += minutes * 60
        }

        if (hours != null) {
            total += hours * 3600
        }

        total *= 1000

        if (total > playing.duration) {
            return event.terminate("The song is not that long! Song duration: ${formatHMS(Duration.ofMillis(playing.duration))}")
        }

        playing.position = total
        event.terminate("Seeked current song to ${formatHMS(Duration.ofMillis(total))}!")
    }
}