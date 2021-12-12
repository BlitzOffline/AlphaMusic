package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.formatHMS
import com.blitzoffline.alphamusic.utils.process
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.slash.sender.SlashSender
import java.time.Duration

@Command("rewind")
@Description("Rewind the current song by a certain amount of time!")
class RewindCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.rewind(
        @Description("Amount of seconds to rewind by!") seconds: Int,
        @Description("Amount of minutes to rewind by!") @Optional minutes: Int?,
        @Description("Amount of hours to rewind by!") @Optional hours: Int?
    ) {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

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

        playing.position = playing.position - total
        event.terminate("Rewound song to: ${formatHMS(Duration.ofMillis(playing.position))}!")
    }
}