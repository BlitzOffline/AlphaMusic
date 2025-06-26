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
import org.apache.log4j.LogManager
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger

class AlphaMusic(
    private val logger: Logger,
    discordToken: String? = null,
    youtubeRefreshToken: String? = null
) {
    private val environmentVariables = EnvironmentVariables(
        discordToken = discordToken,
        youtubeRefreshToken = youtubeRefreshToken
    )
    private val audioPlayerManager = AudioPlayerManager(environmentVariables.youtubeRefreshToken)
    private val trackLoader = TrackLoader()

    private lateinit var commandManager: SlashCommandManager<SlashSender>
    lateinit var jda: JDA
        private set
    lateinit var guildHolder: CachedGuildHolder
        private set
    lateinit var guildManagers: GuildManagersHolder
        private set

    init {
        if (!LogManager.getRootLogger().isDebugEnabled) {
            if (environmentVariables.debugModeEnabled) {
                LogManager.getRootLogger().level = org.apache.log4j.Level.DEBUG
                logger.info("Debug mode enabled.")
            }
        } else {
            logger.info("Debug mode is already enabled.")
        }

        Database(environmentVariables).instance
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Guilds)
        }
    }

    fun run(): AlphaMusic {
        logger.debug("Initializing JDA instance...")
        jda = createJDAInstance()
        jda.awaitReady()

        logger.debug("Configuring JDA exception handling...")
        RestAction.setPassContext(true)
        RestAction.setDefaultFailure(Throwable::printStackTrace)

        logger.debug("Initializing guilds cache...")
        guildHolder = CachedGuildHolder(jda, environmentVariables)
        logger.debug("Initializing guild managers...")
        guildManagers = GuildManagersHolder(jda, trackLoader, audioPlayerManager, guildHolder)

        logger.debug("Initializing slash commands manager...")
        commandManager = SlashCommandManager.create(jda)
        logger.debug("Registering slash command requirements...")
        registerRequirements(commandManager, guildManagers)
        logger.debug("Registering slash command requirement failure messages...")
        registerMessages(commandManager, guildManagers)
        logger.debug("Registering slash commands...")
        registerCommands(commandManager, jda, guildManagers, trackLoader)
        logger.debug("Uploading slash commands...")
        commandManager.pushCommands()

        logger.debug("Initializing paginator...")
        registerPagination(jda)

        logger.debug("Successfully started application...")
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