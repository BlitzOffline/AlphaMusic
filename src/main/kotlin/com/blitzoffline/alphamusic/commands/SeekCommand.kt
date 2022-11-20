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

@Command("seek")
@Description("Seek to a certain moment in the currently playing song!")
class SeekCommand(private val bot: AlphaMusic) : BaseCommand() {
    @Default
    @Requirements(
        Requirement("command_in_guild", messageKey = "command_not_in_guild"),
        Requirement("bot_in_vc", messageKey = "bot_not_in_vc"),
    )
    fun SlashSender.seek(
        @Description("Amount of seconds to seek!") seconds: Int,
        @Description("Amount of minutes to seek!") @Optional minutes: Int?,
        @Description("Amount of hours to seek!") @Optional hours: Int?
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

        if (total > playing.duration) {
            return event.terminate(
                reason = "The song is not that long! Song duration: ${formatHMS(Duration.ofMillis(playing.duration))}",
                ephemeral = true
            )
        }

        playing.position = total
        event.terminate(reason = "Seeked current song to ${formatHMS(Duration.ofMillis(total))}!")
    }
}