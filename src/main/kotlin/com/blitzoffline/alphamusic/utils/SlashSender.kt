package com.blitzoffline.alphamusic.utils

import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.Permission

fun SlashSender.process(
    join: Boolean = false,
    sameChannel: Boolean = false,
    adminBypass: Boolean = false,
    ephemeral: Boolean = false,
    deferred: Boolean = false
): Boolean {
    val guild = guild ?: run {
        event.terminate("This command can only be used in a guild!", ephemeral = ephemeral, deferred = deferred)
        return false
    }
    val member = member ?: run {
        event.terminate("This command can only be used in a guild!", ephemeral = ephemeral, deferred = deferred)
        return false
    }

    val alphaMusic = guild.selfMember
    val memberVC = member.voiceState?.channel
    val alphaMusicVC = alphaMusic.voiceState?.channel

    if (alphaMusicVC == null) {
        if (join) {
            if (memberVC == null) {
                event.terminate("You need to be in a voice channel!", ephemeral = ephemeral, deferred = deferred)
                return false
            }

            if (kotlin.runCatching { guild.audioManager.openAudioConnection(memberVC) }.isFailure) {
                event.terminate("Could not connect to your voice channel!", ephemeral = ephemeral , deferred = deferred)
                return false
            }
        } else {
            event.terminate("The bot is currently not connected to a voice channel!", ephemeral = ephemeral, deferred = deferred)
            return false
        }
    }

    if (sameChannel && alphaMusicVC != memberVC) {
        if (!adminBypass || !member.hasPermission(Permission.ADMINISTRATOR)) {
            event.terminate("You need to be in the same Voice Channel as the bot to do this!", ephemeral = ephemeral, deferred = deferred)
            return false
        }
    }

    return true
}