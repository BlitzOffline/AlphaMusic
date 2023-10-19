package com.blitzoffline.alphamusic.console.command

import com.blitzoffline.alphamusic.console.command.utils.Constants
import org.apache.commons.cli.Options

class StartupCommandParser(options: Options = Constants.STARTUP_COMMAND_OPTIONS): CommandParser(options) {
    fun getDiscordToken(): String? {
        return this.getOptionalArgumentValue("discord_token")
    }

    fun getYoutubeEmail(): String? {
        return this.getOptionalArgumentValue("youtube_email")
    }

    fun getYoutubePassword(): String? {
        return this.getOptionalArgumentValue("youtube_pass")
    }
}