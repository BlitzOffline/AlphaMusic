package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.console.ConsoleApplication
import com.blitzoffline.alphamusic.console.command.StartupCommandParser
import com.blitzoffline.alphamusic.console.command.utils.handleException
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
    val startupCommandParser = StartupCommandParser()

    try {
        LOGGER.debug("Parsing startup arguments: " + args.joinToString(" "))
        startupCommandParser.parseArguments(args)
    } catch (exception: ParseException) {
        val commandLine = args.joinToString(" ")
        if (!handleException(exception, commandLine, startupCommandParser.options, LOGGER, Level.ERROR)) {
            LOGGER.error("Something went wrong while starting the bot.", exception)
            LOGGER.info("Something went wrong while parsing command: $commandLine", exception)
        }

        helpFormatter.printHelp("AlphaMusic.jar", startupCommandParser.options, LOGGER)
        exitProcess(0)
    }

    LOGGER.debug("Initializing application...")
    val bot = AlphaMusic(LOGGER, startupCommandParser.getDiscordToken(), startupCommandParser.getYoutubeEmail(), startupCommandParser.getYoutubePassword())
    LOGGER.debug("Starting application...")
    bot.run()
    LOGGER.debug("Initializing console handler...")
    val console = ConsoleApplication(bot.jda, LOGGER)
    LOGGER.debug("Starting console handler...")
    console.run()
}