package com.blitzoffline.alphamusic.utils

import com.blitzoffline.alphamusic.utils.extension.toTimeUnit
import org.apache.commons.cli.MissingArgumentException
import java.util.concurrent.TimeUnit

data class EnvironmentVariables(
    val discordToken: String,
    val youtubeEmail: String?,
    val youtubePassword: String?,
    val databaseHost: String,
    val databasePort: String,
    val databaseName: String,
    val databaseUsername: String,
    val databasePassword: String,
    val databaseSaveDelay: Long,
    val databaseSaveDelayTimeUnit: TimeUnit,
    val debugModeEnabled: Boolean
) {
    constructor(
        discordToken: String? = null,
        youtubeEmail: String? = null,
        youtubePassword: String? = null,
        databaseHost: String? = null,
        databasePort: String? = null,
        databaseName: String? = null,
        databaseUsername: String? = null,
        databasePassword: String? = null,
        databaseSaveDelay: Long? = null,
        databaseSaveDelayTimeUnit: TimeUnit? = null,
        debugModeEnabled: Boolean? = null
    ) : this(
        discordToken = discordToken ?: System.getenv("ALPHAMUSIC_DISCORD_TOKEN") ?: System.getProperty("ALPHAMUSIC_DISCORD_TOKEN") ?: throw MissingArgumentException("Could not find environment variable ALPHAMUSIC_DISCORD_TOKEN"),
        youtubeEmail = youtubeEmail ?: System.getenv("ALPHAMUSIC_YOUTUBE_EMAIL") ?: System.getProperty("ALPHAMUSIC_YOUTUBE_EMAIL"),
        youtubePassword = youtubePassword ?: System.getenv("ALPHAMUSIC_YOUTUBE_PASSWORD") ?: System.getProperty("ALPHAMUSIC_YOUTUBE_PASSWORD"),
        databaseHost = databaseHost ?: System.getenv("ALPHAMUSIC_MYSQL_HOST") ?: System.getProperty("ALPHAMUSIC_MYSQL_HOST") ?: throw MissingArgumentException("Could not find environment variable ALPHAMUSIC_MYSQL_HOST"),
        databasePort = databasePort ?: System.getenv("ALPHAMUSIC_MYSQL_PORT") ?: System.getProperty("ALPHAMUSIC_MYSQL_PORT") ?: "3306",
        databaseName = databaseName ?: System.getenv("ALPHAMUSIC_MYSQL_DATABASE") ?: System.getProperty("ALPHAMUSIC_MYSQL_DATABASE") ?: throw MissingArgumentException("Could not find environment variable ALPHAMUSIC_MYSQL_DATABASE"),
        databaseUsername = databaseUsername ?: System.getenv("ALPHAMUSIC_MYSQL_USERNAME") ?: System.getProperty("ALPHAMUSIC_MYSQL_USERNAME") ?: throw MissingArgumentException("Could not find environment variable ALPHAMUSIC_MYSQL_USERNAME"),
        databasePassword = databasePassword ?: System.getenv("ALPHAMUSIC_MYSQL_PASSWORD") ?: System.getProperty("ALPHAMUSIC_MYSQL_PASSWORD") ?: throw MissingArgumentException("Could not find environment variable ALPHAMUSIC_MYSQL_PASSWORD"),
        databaseSaveDelay = databaseSaveDelay ?: System.getenv("ALPHAMUSIC_MYSQL_SAVE_DELAY")?.toLong() ?: System.getProperty("ALPHAMUSIC_MYSQL_SAVE_DELAY")?.toLong() ?: 30,
        databaseSaveDelayTimeUnit = databaseSaveDelayTimeUnit ?: (System.getenv("ALPHAMUSIC_MYSQL_SAVE_DELAY_TIMEUNIT") ?: System.getProperty("ALPHAMUSIC_MYSQL_SAVE_DELAY_TIMEUNIT")).toTimeUnit(),
        debugModeEnabled = debugModeEnabled ?: System.getenv("ALPHAMUSIC_DEBUG_MODE")?.toBoolean() ?: System.getProperty("ALPHAMUSIC_DEBUG_MODE")?.toBoolean() ?: false
    )
}