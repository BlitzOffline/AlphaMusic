package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.commands.ClearCommand
import com.blitzoffline.alphamusic.commands.DebugCommand
import com.blitzoffline.alphamusic.commands.ForwardCommand
import com.blitzoffline.alphamusic.commands.GrabCommand
import com.blitzoffline.alphamusic.commands.JoinCommand
import com.blitzoffline.alphamusic.commands.LeaveCommand
import com.blitzoffline.alphamusic.commands.LoopCommand
import com.blitzoffline.alphamusic.commands.NowPlayingCommand
import com.blitzoffline.alphamusic.commands.PauseCommand
import com.blitzoffline.alphamusic.commands.PlayCommand
import com.blitzoffline.alphamusic.commands.QueueCommand
import com.blitzoffline.alphamusic.commands.RadioCommand
import com.blitzoffline.alphamusic.commands.RemoveCommand
import com.blitzoffline.alphamusic.commands.RemoveDupesCommand
import com.blitzoffline.alphamusic.commands.ReplayCommand
import com.blitzoffline.alphamusic.commands.ResumeCommand
import com.blitzoffline.alphamusic.commands.RewindCommand
import com.blitzoffline.alphamusic.commands.SeekCommand
import com.blitzoffline.alphamusic.commands.ShuffleCommand
import com.blitzoffline.alphamusic.commands.SkipCommand
import com.blitzoffline.alphamusic.commands.StopCommand
import com.blitzoffline.alphamusic.commands.VolumeCommand
import com.github.ygimenez.model.PaginatorBuilder
import com.github.ygimenez.type.Emote
import dev.triumphteam.cmd.core.message.MessageKey
import dev.triumphteam.cmd.core.message.context.MessageContext
import dev.triumphteam.cmd.core.requirement.RequirementKey
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class BotReadyListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        PaginatorBuilder.createPaginator()
            .setHandler(bot.jda)
            .shouldRemoveOnReact(false)
            .shouldEventLock(true)
            .setEmote(Emote.CANCEL, "✖️")
            .activate()

        bot.commandManager.registerRequirement(RequirementKey.of("command_in_guild")) { sender ->
            sender.guild != null && sender.member != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("bot_in_vc")) { sender ->
            sender.guild?.selfMember?.voiceState?.channel != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("bot_not_in_vc")) { sender ->
            sender.guild?.selfMember?.voiceState?.channel == null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("member_in_vc")) { sender ->
            sender.member?.voiceState?.channel != null
        }

        bot.commandManager.registerRequirement(RequirementKey.of("same_channel_or_admin")) { sender ->
            val member = sender.member

            if (member == null) {
                false
            } else {
                member.permissions.contains(Permission.ADMINISTRATOR) || member.voiceState?.channel == sender.guild?.selfMember?.voiceState?.channel
            }
        }

        bot.commandManager.registerRequirement(RequirementKey.of("paused")) { sender ->
            val guild = sender.guild
            if (guild == null) {
                true
            } else {
                bot.getMusicManager(guild).player.isPaused
            }
        }

        bot.commandManager.registerRequirement(RequirementKey.of("not_paused")) { sender ->
            val guild = sender.guild
            if (guild == null) {
                false
            } else {
                !bot.getMusicManager(guild).player.isPaused
            }
        }

        bot.commandManager.registerRequirement(RequirementKey.of("requester_or_admin")) { sender ->
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

        bot.commandManager.registerMessage(MessageKey.of("command_not_in_guild", MessageContext::class.java)) { sender, _ ->
            sender.reply("This command can only be used in a guild!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("bot_not_in_vc", MessageContext::class.java)) { sender, _ ->
            sender.reply("The bot is currently not connected to a voice channel!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("bot_already_in_vc", MessageContext::class.java)) { sender, _ ->
            sender.reply("The bot is already connected to a voice channel!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("member_not_in_vc", MessageContext::class.java)) { sender, _ ->
            sender.reply("You need to be connected to a voice channel!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("not_same_channel_or_admin", MessageContext::class.java)) { sender, _ ->
            sender.reply("You need to be in the same Voice Channel as the bot to do this!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("not_paused", MessageContext::class.java)) { sender, _ ->
            sender.reply("The audio is not paused. Use \"/pause\" to pause!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("paused", MessageContext::class.java)) { sender, _ ->
            sender.reply("The audio is already paused. Use \"/resume\" to resume!").queue()
        }

        bot.commandManager.registerMessage(MessageKey.of("not_requester_or_admin", MessageContext::class.java)) { sender, _ ->
            val guild = sender.guild ?: return@registerMessage
            val manager = bot.getMusicManager(guild)
            val playing = manager.player.playingTrack ?: return@registerMessage
            val meta = playing.userData as TrackMetadata

            sender.reply("Only the requester or admins can do this. Requester: ${meta.data.name}#${meta.data.discriminator}").queue()
        }

        bot.jda.guilds.forEach { guild ->
            registerCommands(bot, guild)
        }
    }

    private fun registerCommands(bot: AlphaMusic, guild: Guild) {
        bot.commandManager.registerCommand(
            guild,
            PlayCommand(bot),
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
            DebugCommand(bot)
        )
    }
}