package com.blitzoffline.alphamusic.console

import com.blitzoffline.alphamusic.console.commands.AppConsoleCommand
import com.blitzoffline.alphamusic.console.commands.handleException
import net.dv8tion.jda.api.JDA
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException
import org.slf4j.Logger
import org.slf4j.event.Level
import kotlin.system.exitProcess

class ConsoleApplication(private val jda: JDA, private val logger: Logger) {
    private val commands = listOf("app")
    private var appConsoleCommand = AppConsoleCommand()
    private val helpFormatter = HelpFormatter()

    fun run() {
        while (true) {
            val input = readlnOrNull()
            if (input.isNullOrBlank()) continue

            val lowercaseInput = input.lowercase()

            val command = commands.firstOrNull { lowercaseInput.startsWith(it) }
            if (command == null) {
                logger.warn("Unknown command: $input. Available commands: $commands")
                continue
            }

            if (input.length == command.length) {
                helpFormatter.printHelp(command, appConsoleCommand.options, true)
                continue
            }

            if (!input.substring(command.length).startsWith(" ")) {
                logger.warn("Unknown command: $input. Available commands: $commands")
                continue
            }

            val commandLine = input.substring(command.length + 1)
            // TODO: Handle different commands. For now, only app command is available.

            try {
                appConsoleCommand = appConsoleCommand.parse(commandLine)
            } catch (exception: ParseException) {
                if (!handleException(exception, commandLine, appConsoleCommand.options, logger, Level.WARN)) {
                    logger.warn("Option not found: $commandLine.")
                    logger.debug("Something went wrong while executing command: $commandLine", exception)
                }

                helpFormatter.printHelp(command, appConsoleCommand.options, true)
                continue
            }

            var executed = 0
            if (appConsoleCommand.version()) {
                logger.info("AlphaMusic version: ${this.javaClass.`package`.implementationVersion}")
                executed++
            }

            if (appConsoleCommand.help()) {
                helpFormatter.printHelp(command, appConsoleCommand.options, true)
                executed++
            }

            if (appConsoleCommand.shutdown()) {
                logger.info("Performing shutdown...")
                logger.info("Closing JDA instance...")
                jda.guilds.forEach { it.updateCommands().queue() }
                jda.updateCommands()
                jda.shutdown()
                while (true) {
                    if (jda.status != JDA.Status.SHUTDOWN) continue
                    logger.info("Closed JDA instance successfully! Stopping application...")
                    exitProcess(0)
                }
            }

            if (executed == 0) {
                logger.warn("Options not found: $commandLine.")
                helpFormatter.printHelp(command, appConsoleCommand.options, true)
            }
        }
    }
}