package com.blitzoffline.alphamusic.console.command.utils

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class Constants {
    companion object {
        val APP_CONSOLE_COMMAND_OPTIONS: Options = Options().apply {
            addOption(
                Option.builder("h").longOpt("help").required(false).hasArg(false).desc("Shows this help message")
                    .build()
            )
            addOption(
                Option.builder("s").longOpt("shutdown").required(false).hasArg(false).desc("Shuts down the application")
                    .build()
            )
            addOption(
                Option.builder("v").longOpt("version").required(false).hasArg(false).desc("Shows the current version")
                    .build()
            )
            addOption(
                Option.builder("d").longOpt("debug").required(false).hasArg(true).optionalArg(true).argName("option")
                    .desc("Toggle debug mode").build()
            )
        }

        val STARTUP_COMMAND_OPTIONS = Options().apply {
            addOption(
                Option.builder("dt").longOpt("discord_token").required(false).hasArg(true).argName("token")
                    .desc("Discord bot token").build()
            )
            addOption(
                Option.builder("yrt").longOpt("youtube_refresh_token").required(false).hasArg(true).argName("token")
                    .desc("Youtube refresh token").build()
            )
        }
    }
}
