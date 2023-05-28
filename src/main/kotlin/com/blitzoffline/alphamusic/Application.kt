package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.console.ConsoleApplication
import com.blitzoffline.alphamusic.console.commands.StartupCommand
import com.blitzoffline.alphamusic.console.commands.handleException
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import kotlin.system.exitProcess

private val logger: Logger = LoggerFactory.getLogger(AlphaMusic::class.java)

fun main(args: Array<String>) {
    val helpFormatter = HelpFormatter()
    var startupCommand = StartupCommand()

    try {
        startupCommand = startupCommand.parse(args)
    } catch (exception: ParseException) {
        val commandLine = args.joinToString(" ")
        if (!handleException(exception, commandLine, startupCommand.options, logger, Level.ERROR)) {
            logger.error("Something went wrong while starting the bot.", exception)
            logger.info("Something went wrong while executing command: $commandLine", exception)
        }

        helpFormatter.printHelp("AlphaMusic.jar", startupCommand.options, true)
        exitProcess(0)
    }

    val bot = AlphaMusic(logger, startupCommand.getDiscordToken(), startupCommand.getYoutubeEmail(), startupCommand.getYoutubePassword()).run()
    val console = ConsoleApplication(bot.jda, logger).run()
}