package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.commands.*
import com.blitzoffline.alphamusic.utils.terminate
import com.github.ygimenez.model.PaginatorBuilder
import com.github.ygimenez.type.Emote
import dev.triumphteam.cmd.core.message.MessageKey
import dev.triumphteam.cmd.core.message.context.MessageContext
import dev.triumphteam.cmd.core.requirement.RequirementKey
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class BotReadyListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        registerPagination()

        registerRequirements()
        registerMessages()
        registerCommands()
        //bot.jda.getGuildById("913414011587018782")?.let { bot.commandManager.registerCommand(it, DebugCommand(bot)) }
    }

    private fun registerPagination() {
        PaginatorBuilder.createPaginator()
            .setHandler(bot.jda)
            .shouldRemoveOnReact(false)
            .shouldEventLock(true)
            .setEmote(Emote.CANCEL, "✖️")
            .activate()
    }

    private fun registerRequirements() {
        bot.commandManager.registerRequirement(RequirementKey.of("command_in_guild")) { sender, _ ->
            sender.guild != null && sender.member != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("bot_in_vc")) { sender, _ ->
            sender.guild?.selfMember?.voiceState?.channel != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("member_in_vc")) { sender, _ ->
            sender.member?.voiceState?.channel != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("admin")) { sender, _ ->
            sender.member?.permissions?.contains(Permission.ADMINISTRATOR) ?: false
        }

        bot.commandManager.registerRequirement(RequirementKey.of("same_channel_or_admin")) { sender, _ ->
            val member = sender.member

            if (member == null) {
                false
            } else {
                member.permissions.contains(Permission.ADMINISTRATOR) || member.voiceState?.channel == sender.guild?.selfMember?.voiceState?.channel
            }
        }

        bot.commandManager.registerRequirement(RequirementKey.of("paused")) { sender, _ ->
            val guild = sender.guild
            if (guild == null) {
                true
            } else {
                bot.getMusicManager(guild).player.isPaused
            }
        }

        bot.commandManager.registerRequirement(RequirementKey.of("requester_or_admin")) { sender, _ ->
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
                        val playing = bot.getMusicManager(guild).player.playingTrack
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

    private fun registerMessages() {
        bot.commandManager.registerMessage(MessageKey.of("command_not_in_guild", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "This command can only be used in a guild!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("bot_not_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate("The bot is currently not connected to a voice channel!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("bot_already_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The bot is already connected to a voice channel!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("member_not_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You need to be connected to a voice channel!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("not_same_channel_or_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You need to be in the same Voice Channel as the bot to do this!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("not_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You can't do this!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("not_paused", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The audio is not paused. Use \"/pause\" to pause!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("paused", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The audio is already paused. Use \"/resume\" to resume!", ephemeral = true)
        }

        bot.commandManager.registerMessage(MessageKey.of("not_requester_or_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            val guild = sender.guild ?: return@registerMessage
            val manager = bot.getMusicManager(guild)
            val playing = manager.player.playingTrack ?: return@registerMessage
            val meta = playing.userData as TrackMetadata

            sender.event.terminate(
                reason = "Only the requester or admins can do this. Requester: ${meta.data.name}#${meta.data.discriminator}",
                ephemeral = true
            )
        }
    }

    private fun registerCommands() {
        bot.commandManager.registerCommand(
            HelpCommand(),
            PlayCommand(bot),
            PingCommand(bot),
            LoopCommand(bot),
            QueueCommand(bot),
            NowPlayingCommand(bot),
            GrabCommand(bot),
            ShuffleCommand(bot),
            VolumeCommand(bot),
            PauseCommand(bot),
            ResumeCommand(bot),
            SkipCommand(bot),
            JoinCommand(),
            RemoveDupesCommand(bot),
            ClearCommand(bot),
            ReplayCommand(bot),
            StopCommand(bot),
            RemoveCommand(bot),
            LeaveCommand(bot),
            SeekCommand(bot),
            ForwardCommand(bot),
            RewindCommand(bot),
            RadioCommand(bot),
        )
    }
}