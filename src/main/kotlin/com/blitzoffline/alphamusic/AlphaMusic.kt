package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.database.Database
import com.blitzoffline.alphamusic.database.table.Guilds
import com.blitzoffline.alphamusic.holder.CachedGuildHolder
import com.blitzoffline.alphamusic.holder.GuildManagersHolder
import com.blitzoffline.alphamusic.listener.ShutdownListener
import com.blitzoffline.alphamusic.listener.VoiceListener
import com.blitzoffline.alphamusic.manager.AudioPlayerManager
import com.blitzoffline.alphamusic.track.TrackLoader
import com.blitzoffline.alphamusic.utils.*
import dev.triumphteam.cmd.jda.SlashCommandManager
import dev.triumphteam.cmd.jda.sender.SlashSender
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger

class AlphaMusic(
    private val logger: Logger,
    discordToken: String? = null,
    youtubeEmail: String? = null,
    youtubePassword: String? = null
) {
    private val environmentVariables = EnvironmentVariables(
        discordToken = discordToken,
        youtubeEmail = youtubeEmail,
        youtubePassword = youtubePassword
    )
    private val audioPlayerManager = AudioPlayerManager(youtubeEmail, youtubePassword)
    private val trackLoader = TrackLoader()

    private lateinit var commandManager: SlashCommandManager<SlashSender>
    lateinit var jda: JDA
        private set
    lateinit var guildHolder: CachedGuildHolder
        private set
    lateinit var guildManagers: GuildManagersHolder
        private set

    init {
        Database(environmentVariables).instance
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Guilds)
        }
    }

    fun run(): AlphaMusic {
        jda = createJDAInstance()
        jda.awaitReady()
        RestAction.setPassContext(true)
        RestAction.setDefaultFailure(Throwable::printStackTrace)

        guildHolder = CachedGuildHolder(jda, environmentVariables)
        guildManagers = GuildManagersHolder(jda, trackLoader, audioPlayerManager, guildHolder)

        commandManager = SlashCommandManager.create(jda)
        registerRequirements(commandManager, guildManagers)
        registerMessages(commandManager, guildManagers)
        registerCommands(commandManager, jda, guildManagers, trackLoader)
        commandManager.pushCommands()

        registerPagination(jda)
        return this
    }

    private fun createJDAInstance() = JDABuilder
        .create(
            environmentVariables.discordToken,
            listOf(
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
            )
        )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.SCHEDULED_EVENTS,
        )
        .addEventListeners(
            VoiceListener(this),
            ShutdownListener(this)
        )
        .build()

    companion object {
        const val EMBED_COLOR = 0x70BB2B
    }
}