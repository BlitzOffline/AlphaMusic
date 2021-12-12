package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class GuildMusicManager(bot: AlphaMusic) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(player, bot.jda)

    init {
        player.addListener(audioHandler)
    }
}