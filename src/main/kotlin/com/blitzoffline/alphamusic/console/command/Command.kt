package com.blitzoffline.alphamusic.console.command

import com.blitzoffline.alphamusic.utils.extension.printHelp
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.slf4j.Logger
import org.slf4j.event.Level

abstract class Command(val name: String, options: Options) : CommandParser(options), CommandExecutor {
    private val helpFormatter = HelpFormatter()

    fun printHelp(logger: Logger, level: Level = Level.INFO) {
        helpFormatter.printHelp(name, this.options, logger, level)
    }
}