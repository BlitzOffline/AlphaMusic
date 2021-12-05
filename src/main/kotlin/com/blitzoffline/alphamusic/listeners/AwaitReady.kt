package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.commands.GrabCommand
import com.blitzoffline.alphamusic.commands.LoopCommand
import com.blitzoffline.alphamusic.commands.NowPlayingCommand
import com.blitzoffline.alphamusic.commands.PlayCommand
import com.blitzoffline.alphamusic.commands.QueueCommand
import com.blitzoffline.alphamusic.commands.ShuffleCommand
import com.blitzoffline.alphamusic.commands.VolumeCommand
import com.github.ygimenez.model.PaginatorBuilder
import com.github.ygimenez.type.Emote
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.slash.SlashCommandManager
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AwaitReady(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        PaginatorBuilder.createPaginator()
            .setHandler(bot.jda)
            .shouldRemoveOnReact(false)
            .shouldEventLock(true)
            .setEmote(Emote.CANCEL, "✖️")
            .activate()

        val guild = bot.jda.guilds.first() ?: return bot.logger.warn("Couldn't find any guilds!")
        bot.manager.registerCommands(
            guild,
            PlayCommand(bot),
            LoopCommand(bot),
            QueueCommand(bot),
            NowPlayingCommand(bot),
            GrabCommand(bot),
            ShuffleCommand(bot),
            VolumeCommand(bot)
        
        )
    }

    private fun SlashCommandManager<SlashSender>.registerCommands(guild: Guild, vararg commands: BaseCommand) =
        commands.forEach { registerCommand(guild, it) }
}