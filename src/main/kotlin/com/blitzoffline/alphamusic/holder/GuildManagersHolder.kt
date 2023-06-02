package com.blitzoffline.alphamusic.holder

import com.blitzoffline.alphamusic.manager.AudioPlayerManager
import com.blitzoffline.alphamusic.track.TrackLoader
import com.blitzoffline.alphamusic.manager.GuildManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild

class GuildManagersHolder(
    private val jda: JDA,
    private val trackLoader: TrackLoader,
    private val audioPlayerManager: AudioPlayerManager,
    private val guildHolder: CachedGuildHolder,
) {
    private val musicManagers = HashMap<String, GuildManager>()

    @Synchronized
    fun getGuildManager(guild: Guild): GuildManager {
        var musicManager = musicManagers[guild.id]

        if (musicManager == null) {
            musicManager = GuildManager(guild.id, jda, trackLoader, audioPlayerManager, guildHolder)
            musicManagers[guild.id] = musicManager
        }

        guild.audioManager.sendingHandler = musicManager.audioHandler
        return musicManager
    }
}