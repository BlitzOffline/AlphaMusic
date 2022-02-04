package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.votes.VoteHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import net.dv8tion.jda.api.entities.Guild

class GuildMusicManager(bot: AlphaMusic, val guild: Guild) {
    val player: AudioPlayer = bot.playerManager.createPlayer()
    val audioHandler = AudioHandler(bot, player, guild)
    val voteHandler = VoteHandler()

    init {
        player.addListener(audioHandler)
    }
}