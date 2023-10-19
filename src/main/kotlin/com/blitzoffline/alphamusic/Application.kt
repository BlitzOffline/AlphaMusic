package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.console.ConsoleApplication
import com.blitzoffline.alphamusic.console.command.StartupCommand
import com.blitzoffline.alphamusic.console.command.handleException
import com.blitzoffline.alphamusic.utils.extension.printHelp
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import kotlin.system.exitProcess

private val LOGGER: Logger = LoggerFactory.getLogger(AlphaMusic::class.java)

fun main(args: Array<String>) {
    val helpFormatter = HelpFormatter()
    var startupCommand = StartupCommand()

    try {
        LOGGER.debug("Parsing startup arguments: " + args.joinToString(" "))
        startupCommand = startupCommand.parse(args)
    } catch (exception: ParseException) {
        val commandLine = args.joinToString(" ")
        if (!handleException(exception, commandLine, startupCommand.options, LOGGER, Level.ERROR)) {
            LOGGER.error("Something went wrong while starting the bot.", exception)
            LOGGER.info("Something went wrong while executing command: $commandLine", exception)
        }

        helpFormatter.printHelp("AlphaMusic.jar", startupCommand.options, LOGGER)
        exitProcess(0)
    }

    LOGGER.debug("Initializing application...")
    val bot = AlphaMusic(LOGGER, startupCommand.getDiscordToken(), startupCommand.getYoutubeEmail(), startupCommand.getYoutubePassword())
    LOGGER.debug("Starting application...")
    bot.run()
    LOGGER.debug("Initializing console handler...")
    val console = ConsoleApplication(bot.jda, LOGGER)
    LOGGER.debug("Starting console handler...")
    console.run()
}