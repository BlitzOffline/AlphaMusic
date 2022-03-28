package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.console.ConsoleCommands
import com.blitzoffline.alphamusic.utils.StartupParametersParser

fun main(args: Array<String>) {
    val startupParametersParser = StartupParametersParser(args)
    val bot = AlphaMusic(
        startupParametersParser.parseDiscordBotToken(),
        startupParametersParser.parseYoutubeEmail(),
        startupParametersParser.parseYoutubePassword()
    )

    bot.run()
    ConsoleCommands(bot).run()
}