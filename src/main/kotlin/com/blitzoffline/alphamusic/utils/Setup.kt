package com.blitzoffline.alphamusic.utils

import com.blitzoffline.alphamusic.track.TrackLoader
import com.blitzoffline.alphamusic.track.TrackMetadata
import com.blitzoffline.alphamusic.command.*
import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.utils.extension.terminate
import com.github.ygimenez.model.PaginatorBuilder
import com.github.ygimenez.type.Emote
import dev.triumphteam.cmd.core.message.MessageKey
import dev.triumphteam.cmd.core.message.context.MessageContext
import dev.triumphteam.cmd.core.requirement.RequirementKey
import dev.triumphteam.cmd.jda.SlashCommandManager
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import dev.triumphteam.cmd.jda.sender.SlashSender
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission

fun registerPagination(jda: JDA) {
    PaginatorBuilder.createPaginator()
        .setHandler(jda)
        .shouldRemoveOnReact(false)
        .shouldEventLock(true)
        .setEmote(Emote.CANCEL, "✖️")
        .activate()
}

fun registerRequirements(commandManager: SlashCommandManager<SlashSender>, musicManagers: GuildManagersHolder) {
    commandManager.registerRequirement(RequirementKey.of("command_in_guild")) { sender, _ ->
        sender.guild != null && sender.member != null
    }

    commandManager.registerRequirement(RequirementKey.of("bot_in_vc")) { sender, _ ->
        sender.guild?.selfMember?.voiceState?.channel != null
    }

    commandManager.registerRequirement(RequirementKey.of("member_in_vc")) { sender, _ ->
        sender.member?.voiceState?.channel != null
    }

    commandManager.registerRequirement(RequirementKey.of("admin")) { sender, _ ->
        sender.member?.permissions?.contains(Permission.ADMINISTRATOR) ?: false
    }

    commandManager.registerRequirement(RequirementKey.of("same_channel_or_admin")) { sender, _ ->
        val member = sender.member

        if (member == null) {
            false
        } else {
            member.permissions.contains(Permission.ADMINISTRATOR) || member.voiceState?.channel == sender.guild?.selfMember?.voiceState?.channel
        }
    }

    commandManager.registerRequirement(RequirementKey.of("paused")) { sender, _ ->
        val guild = sender.guild
        if (guild == null) {
            true
        } else {
            val guildMusicManager = musicManagers.getGuildManager(guild) ?: return@registerRequirement true

            musicManagers.getGuildManager(guild).audioPlayer.isPaused
        }
    }

    commandManager.registerRequirement(RequirementKey.of("requester_or_admin")) { sender, _ ->
        val guild = sender.guild
        if (guild == null) {
            false
        } else {
            val member = sender.member
            if (member == null) {
                false
            } else {
                if (member.hasPermission(Permission.ADMINISTRATOR)) {
                    true
                } else {
                    val playing = musicManagers.getGuildManager(guild).audioPlayer.playingTrack
                    if (playing == null) {
                        true
                    } else {
                        val meta = playing.userData as TrackMetadata
                        meta.data.id == member.id
                    }
                }
            }
        }
    }
}

fun registerMessages(commandManager: SlashCommandManager<SlashSender>, musicManagers: GuildManagersHolder) {
    commandManager.registerMessage(MessageKey.of("command_not_in_guild", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "This command can only be used in a guild!", ephemeral = true)
    }

    commandManager.registerMessage(MessageKey.of("bot_not_in_vc", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate("The bot is currently not connected to a voice channel!", ephemeral = true)
    }

    commandManager.registerMessage(MessageKey.of("bot_already_in_vc", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "The bot is already connected to a voice channel!", ephemeral = true)
    }

    commandManager.registerMessage(MessageKey.of("member_not_in_vc", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "You need to be connected to a voice channel!", ephemeral = true)
    }

    commandManager.registerMessage(
        MessageKey.of(
            "not_same_channel_or_admin",
            MessageContext::class.java
        )
    ) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(
            reason = "You need to be in the same Voice Channel as the bot to do this!",
            ephemeral = true
        )
    }

    commandManager.registerMessage(MessageKey.of("not_admin", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "You can't do this!", ephemeral = true)
    }

    commandManager.registerMessage(MessageKey.of("not_paused", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "The audio is not paused. Use \"/pause\" to pause!", ephemeral = true)
    }

    commandManager.registerMessage(MessageKey.of("paused", MessageContext::class.java)) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        sender.event.terminate(reason = "The audio is already paused. Use \"/resume\" to resume!", ephemeral = true)
    }

    commandManager.registerMessage(
        MessageKey.of(
            "not_requester_or_admin",
            MessageContext::class.java
        )
    ) { sender, _ ->
        if (sender !is SlashCommandSender) return@registerMessage
        val guild = sender.guild ?: return@registerMessage
        val manager = musicManagers.getGuildManager(guild)
        val playing = manager.audioPlayer.playingTrack ?: return@registerMessage
        val meta = playing.userData as TrackMetadata

        sender.event.terminate(
            reason = "Only the requester or admins can do this. Requester: ${meta.data.name}#${meta.data.discriminator}",
            ephemeral = true
        )
    }
}

fun registerCommands(commandManager: SlashCommandManager<SlashSender>, jda: JDA, guildManagersHolder: GuildManagersHolder, trackLoader: TrackLoader) {
    commandManager.registerCommand(
        HelpCommand(),
        PlayCommand(trackLoader, guildManagersHolder),
        PingCommand(jda),
        LoopCommand(guildManagersHolder),
        QueueCommand(guildManagersHolder),
        NowPlayingCommand(guildManagersHolder),
        GrabCommand(guildManagersHolder),
        ShuffleCommand(guildManagersHolder),
        VolumeCommand(guildManagersHolder),
        PauseCommand(guildManagersHolder),
        ResumeCommand(guildManagersHolder),
        SkipCommand(guildManagersHolder),
        JoinCommand(),
        RemoveDupesCommand(guildManagersHolder),
        ClearCommand(guildManagersHolder),
        ReplayCommand(guildManagersHolder),
        StopCommand(guildManagersHolder),
        RemoveCommand(guildManagersHolder),
        LeaveCommand(guildManagersHolder),
        SeekCommand(guildManagersHolder),
        ForwardCommand(guildManagersHolder),
        RewindCommand(guildManagersHolder),
        RadioCommand(guildManagersHolder),
    )
}