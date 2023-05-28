package com.blitzoffline.alphamusic.console.commands

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class AppConsoleCommand(options: Options = defaultConsoleCommandOptions()) : CommandParser(options) {
    override fun parse(args: Array<String>): AppConsoleCommand {
        super.parse(args)
        return this
    }

    override fun parse(args: String): AppConsoleCommand {
        super.parse(args)
        return this
    }

    fun help(): Boolean {
        return this.isPresent("help")
    }

    fun shutdown(): Boolean {
        return this.isPresent("shutdown")
    }

    fun version(): Boolean {
        return this.isPresent("version")
    }

    fun debug(): Boolean {
        return this.isPresent("debug")
    }
}

private fun defaultConsoleCommandOptions(): Options {
    return Options().apply {
        addOption(Option.builder("h").longOpt("help").required(false).hasArg(false).desc("Shows this help message").build())
        addOption(Option.builder("s").longOpt("shutdown").required(false).hasArg(false).desc("Shuts down the application").build())
        addOption(Option.builder("v").longOpt("version").required(false).hasArg(false).desc("Shows the current version").build())
        addOption(Option.builder("d").longOpt("debug").required(false).hasArg(false).desc("Toggle debug mode").build())
    }
}