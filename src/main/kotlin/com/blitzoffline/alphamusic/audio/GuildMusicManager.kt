package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.votes.VoteHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class GuildMusicManager(bot: AlphaMusic, guildId: String) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(bot, player, guildId)
    val voteHandler = VoteHandler()

    init {
        player.addListener(audioHandler)
    }
}