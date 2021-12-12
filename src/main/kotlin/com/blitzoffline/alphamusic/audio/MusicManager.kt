package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import net.dv8tion.jda.api.entities.Guild

class MusicManager(bot: AlphaMusic, val guild: Guild) {
    // todo: Cancel leave task (if there is one) when a track is loaded
    // todo: Add a leave task when a track stops playing
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(player)

    init {
        player.addListener(audioHandler)
    }
}