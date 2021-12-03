package com.blitzoffline.alphamusic.utils

import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.Permission

fun SlashSender.process(): Boolean {
    val guild = guild ?: run {
        event.terminate("This command can only be used in a guild!", ephemeral = false)
        return false
    }
    val member = member ?: run {
        event.terminate("This command can only be used in a guild!", ephemeral = false)
        return false
    }

    val alphaMusic = guild.selfMember
    val memberVC = member.voiceState?.channel
    val alphaMusicVC = alphaMusic.voiceState?.channel

    if (alphaMusicVC == null) {
        event.terminate("The bot is currently not connected to a voice channel!")
        return false
    }

    if (alphaMusicVC != memberVC && !member.hasPermission(Permission.ADMINISTRATOR)) {
        event.terminate("You need to be in the same Voice Channel as the bot to do this!", ephemeral = false)
        return false
    }

    return true
}