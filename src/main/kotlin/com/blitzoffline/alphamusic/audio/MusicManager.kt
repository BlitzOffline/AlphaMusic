package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import net.dv8tion.jda.api.entities.Guild

class MusicManager(bot: AlphaMusic, val guild: Guild) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(bot, player, guild)

    init {
        player.addListener(audioHandler)
    }
}