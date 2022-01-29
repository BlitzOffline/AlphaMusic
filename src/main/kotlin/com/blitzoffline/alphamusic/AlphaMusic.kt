package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.audio.MusicManager
import com.blitzoffline.alphamusic.audio.PlayerManager
import com.blitzoffline.alphamusic.audio.TrackService
import com.blitzoffline.alphamusic.listeners.BotReadyListener
import com.blitzoffline.alphamusic.listeners.VoiceChannelListener
import com.blitzoffline.alphamusic.managers.TasksManager
import dev.triumphteam.cmd.slash.SlashCommandManager
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AlphaMusic(private val token: String, youtubeEmail: String?, youtubePass: String?) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    val playerManager = PlayerManager(youtubeEmail, youtubePass)
    val trackService = TrackService(this)
    val tasksManager = TasksManager()
    private val musicManagers = HashMap<String, MusicManager>()

    lateinit var manager: SlashCommandManager<SlashSender>
    lateinit var jda: JDA
        private set

    fun run() {
        jda = createJDAInstance()
        manager = SlashCommandManager.create(jda)
    }

    private fun createJDAInstance() = JDABuilder
        .create(
            token,
            listOf(
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
            )
        )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS
        )
        .addEventListeners(
            BotReadyListener(this),
            VoiceChannelListener(this)
        )
        .build()

    @Synchronized fun getMusicManager(guild: Guild): MusicManager {
        var musicManager = musicManagers[guild.id]

        if (musicManager == null) {
            musicManager = MusicManager(this, guild)
            musicManagers[guild.id] = musicManager
        }

        guild.audioManager.sendingHandler = musicManager.audioHandler
        return musicManager
    }
}