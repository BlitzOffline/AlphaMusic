package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.utils.formatHMSDouble
import com.blitzoffline.alphamusic.utils.progressBar
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import java.time.Duration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

class GuildMusicManager(bot: AlphaMusic) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(player, bot.jda)

    init {
        player.addListener(audioHandler)
    }
}