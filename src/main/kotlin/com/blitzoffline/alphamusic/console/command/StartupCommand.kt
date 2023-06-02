package com.blitzoffline.alphamusic.console.command

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class StartupCommand(options: Options = defaultStartupCommandOptions()): CommandParser(options) {
    override fun parse(args: Array<String>): StartupCommand {
        super.parse(args)
        return this
    }

    override fun parse(args: String): StartupCommand {
        super.parse(args)
        return this
    }

    fun getDiscordToken(): String? {
        return this.getOptionalValue("discord_token")
    }

    fun getYoutubeEmail(): String? {
        return this.getOptionalValue("youtube_email")
    }

    fun getYoutubePassword(): String? {
        return this.getOptionalValue("youtube_pass")
    }
}

private fun defaultStartupCommandOptions(): Options {
    return Options().apply {
        addOption(Option.builder("dt").longOpt("discord_token").required(false).hasArg(true).argName("token").desc("Discord bot token").build())
        addOption(Option.builder("ye").longOpt("youtube_email").required(false).hasArg(true).argName("email").desc("Youtube account email").build())
        addOption(Option.builder("yp").longOpt("youtube_pass").required(false).hasArg(true).argName("password").desc("Youtube account password").build())
    }
}