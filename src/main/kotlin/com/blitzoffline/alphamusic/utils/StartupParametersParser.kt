package com.blitzoffline.alphamusic.utils

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class StartupParametersParser(args: Array<String>) {
    private val cli = DefaultParser().parse(
        Options().apply {
            addOption(
                Option.builder("discord-bot-token").hasArg().argName("discord-bot-token").required(true).build()
            )
            addOption(
                Option.builder("youtube-email").hasArg().argName("youtube-email").required(false).build()
            )
            addOption(
                Option.builder("youtube-password").hasArg().argName("youtube-pass").required(false).build()
            )
            addOption(
                Option.builder("spotify-api-token").hasArg().argName("spotify-api-token").required(false).build()
            )
        },
        args
    )

    fun parseDiscordBotToken(): String {
        return cli.getOptionValue("discord-bot-token")
    }

    fun parseYoutubeEmail(): String? {
        return cli.getOptionValue("youtube-email")
    }

    fun parseYoutubePassword(): String? {
        return cli.getOptionValue("youtube-pass")
    }

    fun parseSpotifyAPIToken(): String? {
        return cli.getOptionValue("spotify-api-token")
    }
}