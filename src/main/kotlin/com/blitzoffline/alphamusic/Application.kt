package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.utils.CommandLine

fun main(args: Array<String>) {
    val cli = CommandLine(args)
    val bot = AlphaMusic(cli.fetchTokenFromFlag(), cli.fetchEmailFromFlag(), cli.fetchPassFromFlag())
    bot.run()
}