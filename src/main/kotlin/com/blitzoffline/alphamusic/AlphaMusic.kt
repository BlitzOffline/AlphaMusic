package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.audio.GuildMusicManager
import com.blitzoffline.alphamusic.audio.PlayerManager
import com.blitzoffline.alphamusic.settings.SettingsHandler
import com.blitzoffline.alphamusic.settings.holders.Bot
import dev.triumphteam.cmd.slash.SlashCommandManager
import me.mattstudios.config.SettingsManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AlphaMusic {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val playerManager = PlayerManager()

    private val musicManagers = HashMap<String, GuildMusicManager>()

    lateinit var settings: SettingsManager
        private set

    lateinit var jda: JDA
        private set

    fun run() {
        val settingsHandler = SettingsHandler(this)
        settings = settingsHandler.fetchSettings()

        jda = createJDAInstance().awaitReady()

        val slashCommandManager = SlashCommandManager.createDefault(jda)
    }

    private fun createJDAInstance() = JDABuilder
        .create(
            settings[Bot.TOKEN],
            listOf(
                GatewayIntent.GUILD_VOICE_STATES
            )
        )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS
        )
        .build()

    @Synchronized fun getGuildMusicManager(guild: Guild): GuildMusicManager {
        var musicManager = musicManagers[guild.id]

        if (musicManager == null) {
            musicManager = GuildMusicManager(this)
            musicManagers[guild.id] = musicManager
        }

        guild.audioManager.sendingHandler = musicManager.audioHandler
        return musicManager
    }
}