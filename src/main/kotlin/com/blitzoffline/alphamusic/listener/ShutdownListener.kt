package com.blitzoffline.alphamusic.listener

import com.blitzoffline.alphamusic.AlphaMusic
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ShutdownListener(private val bot: AlphaMusic) : ListenerAdapter() {
    override fun onShutdown(event: ShutdownEvent) {
        bot.guildHolder.save()
    }
}