package com.blitzoffline.alphamusic.utils

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class StartupParametersParser(args: Array<String>) {
    private val cli = DefaultParser().parse(
        Options().apply {
            addOption(
                Option.builder("discord_bot_token").hasArg().argName("discord_bot_token").required(true).build()
            )
            addOption(
                Option.builder("youtube_email").hasArg().argName("youtube_email").required(false).build()
            )
            addOption(
                Option.builder("youtube_password").hasArg().argName("youtube_pass").required(false).build()
            )
            addOption(
                Option.builder("spotify_api_token").hasArg().argName("spotify_api_token").required(false).build()
            )
        },
        args
    )

    fun parseDiscordBotToken(): String {
        return cli.getOptionValue("discord_bot_token")
    }

    fun parseYoutubeEmail(): String? {
        return cli.getOptionValue("youtube_email")
    }

    fun parseYoutubePassword(): String? {
        return cli.getOptionValue("youtube_pass")
    }

    fun parseSpotifyAPIToken(): String? {
        return cli.getOptionValue("spotify_api_token")
    }
}