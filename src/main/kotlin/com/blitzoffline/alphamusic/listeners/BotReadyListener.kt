package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.commands.ClearCommand
import com.blitzoffline.alphamusic.commands.ForwardCommand
import com.blitzoffline.alphamusic.commands.GrabCommand
import com.blitzoffline.alphamusic.commands.JoinCommand
import com.blitzoffline.alphamusic.commands.LeaveCommand
import com.blitzoffline.alphamusic.commands.LoopCommand
import com.blitzoffline.alphamusic.commands.NowPlayingCommand
import com.blitzoffline.alphamusic.commands.PauseCommand
import com.blitzoffline.alphamusic.commands.PlayCommand
import com.blitzoffline.alphamusic.commands.QueueCommand
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

        bot.manager.registerRequirement(RequirementKey.of("IN_GUILD")) { sender ->
            sender.guild != null && sender.member != null
        }

        bot.manager.registerRequirement(RequirementKey.of("BOT_IS_IN_VC")) { sender ->
            sender.guild?.selfMember?.voiceState?.channel != null
        }

        bot.manager.registerRequirement(RequirementKey.of("SAME_CHANNEL_OR_ADMIN")) { sender ->
            val member = sender.member

            if (member == null) {
                false
            } else {
                member.permissions.contains(Permission.ADMINISTRATOR) || member.voiceState?.channel == sender.guild?.selfMember?.voiceState?.channel
            }
        }

        bot.manager.registerMessage(MessageKey.of("IN_GUILD", MessageContext::class.java)) { sender, _ ->
            sender.reply("This command can only be used in a guild!").queue()
        }

        bot.manager.registerMessage(MessageKey.of("BOT_IS_IN_VC", MessageContext::class.java)) { sender, _ ->
            sender.reply("The bot is currently not connected to a voice channel!").queue()
        }

        bot.manager.registerMessage(MessageKey.of("SAME_CHANNEL_OR_ADMIN", MessageContext::class.java)) { sender, _ ->
            sender.reply("You need to be in the same Voice Channel as the bot to do this!").queue()
        }

        val guild = bot.jda.guilds.first() ?: return bot.logger.warn("Couldn't find any guilds!")
        bot.manager.registerCommand(
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
            LeaveCommand(),
            SeekCommand(bot),
            ForwardCommand(bot),
            RewindCommand(bot),
        )
    }
}