package com.blitzoffline.alphamusic.console.command

import com.blitzoffline.alphamusic.console.command.utils.Constants
import com.blitzoffline.alphamusic.console.command.utils.handleException
import net.dv8tion.jda.api.JDA
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.log4j.LogManager
import org.slf4j.Logger
import org.slf4j.event.Level
import java.time.Duration
import kotlin.system.exitProcess

class AppConsoleCommand(
    commandName: String,
    private val jda: JDA,
    private val logger: Logger,
    options: Options = Constants.APP_CONSOLE_COMMAND_OPTIONS
) : Command(commandName, options) {
    override fun execute(arguments: String?) {
        if (arguments == null) {
            return this.printHelp(logger)
        }

        try {
            this.parseArguments(arguments)
        } catch (exception: ParseException) {
            if (!handleException(exception, arguments, this.options, this.logger, Level.WARN)) {
                logger.warn("Option not found: $arguments.")
                logger.debug("Something went wrong while executing command: $arguments", exception)
            }

            return this.printHelp(logger)
        }

        var executedCommands = 0

        if (this.debug()) {
            val debugInfo = this.debugInfo()
            if (!debugInfo.isNullOrBlank() && !debugInfo.equals("true", true) && !debugInfo.equals("false", true)) {
                when(debugInfo) {
                    "guilds" -> {
                        logger.info("Bot is currently in ${jda.guilds.size} guilds.")
                    }
                    "members" -> {
                        logger.info("Bot is currently serving ${jda.guilds.sumOf { it.memberCount }} members.")
                    }
                    "voice" -> {
                        logger.info("Bot is currently connected to ${jda.audioManagers.count { it.isConnected }} voice channels.")
                    }
                    "voice-users" -> {
                        // Count number of users that are currently connected to a voice channel together with the unmuted bot.
                        logger.info("Bot is currently serving ${jda.audioManagers
                            .filter { it.isConnected && it.guild.selfMember.voiceState?.isMuted == false && it.guild.selfMember.voiceState?.isDeafened == false }
                            .sumOf { it.connectedChannel?.members?.size?.minus(1) ?: 0 }} users in voice channels.")
                    }
                }
            } else {
                val toggle = debugInfo.isNullOrBlank()
                val turnOn = if (toggle) !LogManager.getRootLogger().isDebugEnabled else debugInfo.toBoolean()

                if (turnOn) {
                    LogManager.getRootLogger().level = org.apache.log4j.Level.DEBUG
                    logger.info("Debug mode enabled.")
                } else {
                    LogManager.getRootLogger().level = org.apache.log4j.Level.INFO
                    logger.info("Debug mode disabled.")
                }
            }
            executedCommands++
        }

        if (this.version()) {
            logger.info("AlphaMusic version: ${this.javaClass.`package`.implementationVersion}")
            executedCommands++
        }

        if (this.help()) {
            this.printHelp(logger)
            executedCommands++
        }

        if (this.shutdown()) {
            logger.info("Performing shutdown...")
            logger.info("Closing JDA instance...")
            jda.guilds.forEach { it.updateCommands().queue() }
            jda.updateCommands()

            // Allow at most 10 seconds for remaining requests to finish
            if (!jda.awaitShutdown(Duration.ofSeconds(15))) {
                jda.shutdownNow()
                // Cancel all remaining requests
                jda.awaitShutdown(); // Wait until shutdown is complete (indefinitely)
            }

            logger.info("Closed JDA instance successfully! Stopping application...")
            exitProcess(0)
        }

        if (executedCommands == 0) {
            logger.warn("Options not found: $arguments.")
            return this.printHelp(logger)
        }
    }

    private fun help(): Boolean {
        return this.isArgumentPresent("help")
    }

    private fun shutdown(): Boolean {
        return this.isArgumentPresent("shutdown")
    }

    private fun version(): Boolean {
        return this.isArgumentPresent("version")
    }

    private fun debug(): Boolean {
        return this.isArgumentPresent("debug")
    }

    private fun debugInfo(): String? {
        if (!this.debug()) return null
        return this.getOptionalArgumentValue("debug")
    }
}