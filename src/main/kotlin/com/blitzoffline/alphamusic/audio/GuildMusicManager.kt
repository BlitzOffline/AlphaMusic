package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.vote.VoteManager
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class GuildMusicManager(bot: AlphaMusic, val guildId: String) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(bot, player, guildId)
    val voteManager = VoteManager()

    init {
        player.addListener(audioHandler)
    }
}