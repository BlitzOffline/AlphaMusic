package com.blitzoffline.alphamusic.console.command

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

/**
 * Simple command parser.
 * @param options argument settings for the command.
 */
open class CommandParser(val options: Options) {
    private val parser = DefaultParser.builder()
        .setAllowPartialMatching(true)
        .setStripLeadingAndTrailingQuotes(true)
        .build()

    private lateinit var commandLine: CommandLine

    /**
     * Parse a set of arguments based on the settings selected in the constructor of the parser.
     * @param args the arguments to be parsed based on the settings
     */
    fun parseArguments(args: Array<String>) {
        commandLine = parser.parse(options, args)
    }

    /**
     * Helper method for [CommandParser.parseArguments]. The arguments string will be split at spaces.
     * Parse a set of arguments based on the settings selected in the constructor of the parser.
     * @param args the arguments to be parsed based on the settings
     */
    fun parseArguments(args: String) {
        this.parseArguments(args.split(" ").toTypedArray())
    }

    fun isArgumentPresent(option: String): Boolean {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.hasOption(option)
    }

    fun getArgumentValue(option: String): String {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.getOptionValue(option)
    }

    fun getOptionalArgumentValue(option: String): String? {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.getOptionValue(option)
    }
}