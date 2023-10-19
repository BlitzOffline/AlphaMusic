package com.blitzoffline.alphamusic.console

import com.blitzoffline.alphamusic.console.command.AppConsoleCommand
import com.blitzoffline.alphamusic.console.command.handleException
import com.blitzoffline.alphamusic.utils.extension.printHelp
import net.dv8tion.jda.api.JDA
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException
import org.apache.log4j.LogManager
import org.slf4j.Logger
import org.slf4j.event.Level
import java.time.Duration
import kotlin.system.exitProcess

class ConsoleApplication(private val jda: JDA, private val logger: Logger) {
    private val commandParsers = mapOf("app" to AppConsoleCommand())
    private val helpFormatter = HelpFormatter()

    fun run() {
        logger.debug("Successfully started console handler...")
        while (true) {
            val input = readlnOrNull()
            if (input.isNullOrBlank()) continue

            val splitInput = input.split(" ", limit = 2)
            val command = splitInput[0]
            val localCommand = command.lowercase()

            var commandParser = commandParsers[localCommand]
            if (commandParser == null) {
                logger.warn("Unknown command: ${command}. Available commands: ${commandParsers.keys.joinToString(", ")}")
                continue
            }

            if (splitInput.size < 2) {
                helpFormatter.printHelp(localCommand, commandParser.options, logger)
                continue
            }
            val arguments = splitInput[1]

            try {
                commandParser = commandParser.parse(arguments)
            } catch (exception: ParseException) {
                if (!handleException(exception, arguments, commandParser.options, logger, Level.WARN)) {
                    logger.warn("Option not found: $arguments.")
                    logger.debug("Something went wrong while executing command: $arguments", exception)
                }

                helpFormatter.printHelp(localCommand, commandParser.options, logger)
                continue
            }

            var executed = 0
            if (commandParser.debug()) {
                val debugInfo = commandParser.debugInfo()
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
                executed++
            }

            if (commandParser.version()) {
                logger.info("AlphaMusic version: ${this.javaClass.`package`.implementationVersion}")
                executed++
            }

            if (commandParser.help()) {
                helpFormatter.printHelp(localCommand, commandParser.options, logger)
                executed++
            }

            if (commandParser.shutdown()) {
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

            if (executed == 0) {
                logger.warn("Options not found: $arguments.")
                helpFormatter.printHelp(localCommand, commandParser.options, logger)
            }
        }
    }
}