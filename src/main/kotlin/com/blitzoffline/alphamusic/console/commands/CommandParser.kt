package com.blitzoffline.alphamusic.console.commands

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

open class CommandParser(val options: Options) {
    private val parser = DefaultParser.builder()
        .setAllowPartialMatching(true)
        .setStripLeadingAndTrailingQuotes(true)
        .build()

    private lateinit var commandLine: CommandLine

    open fun parse(args: Array<String>): CommandParser {
        commandLine = parser.parse(options, args)
        return this
    }

    open fun parse(args: String): CommandParser {
        return this.parse(args.split(" ").toTypedArray())
    }

    fun isPresent(option: String): Boolean {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.hasOption(option)
    }

    fun getValue(option: String): String {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.getOptionValue(option)
    }

    fun getOptionalValue(option: String): String? {
        if (!::commandLine.isInitialized) {
            throw IllegalStateException("No arguments specified!")
        }

        return commandLine.getOptionValue(option)
    }
}