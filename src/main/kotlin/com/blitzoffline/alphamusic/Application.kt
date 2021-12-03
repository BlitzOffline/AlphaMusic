package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.utils.fetchTokenFromFlag

fun main(args: Array<String>) {
    val bot = AlphaMusic(fetchTokenFromFlag(args))
    bot.run()
}