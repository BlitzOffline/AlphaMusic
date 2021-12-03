package com.blitzoffline.alphamusic.listeners

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.commands.LoopCommand
import com.blitzoffline.alphamusic.commands.PlayCommand
import com.blitzoffline.alphamusic.commands.QueueCommand
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AwaitReady(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        val guild = bot.jda.guilds.first() ?: return bot.logger.warn("Couldn't find any guilds!")
        bot.manager.registerCommand(
            guild,
            PlayCommand(bot)
        )
        bot.manager.registerCommand(
            guild,
            LoopCommand(bot),
        )
        bot.manager.registerCommand(
            guild,
            QueueCommand(bot)
        )
    }
}