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

@Command("seek")
@Description("Seek to a certain moment in the song!")
class SeekCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    fun SlashSender.foo(seconds: Int, @Optional minutes: Int?, @Optional hours: Int?) {
        if (!process(sameChannel = true, adminBypass = true)) {
            return
        }

        if (seconds <= 0) {
            return event.terminate("Seconds can not be <= 0!")
        }

        if (minutes != null && minutes < 0) {
            return event.terminate("Minutes can not be < 0!")
        }

        if (hours != null && hours < 0) {
            return event.terminate("Hours can not be < 0!")
        }

        val guild = guild ?: return
        val musicManager = bot.getGuildMusicManager(guild)

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