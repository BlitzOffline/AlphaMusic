package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.AudioLoaderResultHandler
import com.blitzoffline.alphamusic.utils.process
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.slash.sender.SlashSender

@Command("play")
@Description("Play a song!")
class PlayCommand(private val bot: AlphaMusic) : BaseCommand() {
    @SubCommand("link")
    @Description("Find a song using a link!")
    fun SlashSender.link(link: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getGuildMusicManager(guild)
        bot.playerManager.loadItemOrdered(musicManager.player, link, AudioLoaderResultHandler(event, musicManager, true))
    }

    @SubCommand("youtube")
    @Description("Find a song on youtube using keywords!")
    fun SlashSender.youtube(keywords: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getGuildMusicManager(guild)
        bot.playerManager.loadItemOrdered(musicManager.player, "ytsearch:$keywords", AudioLoaderResultHandler(event, musicManager, true))
    }

    @SubCommand("youtube-music")
    @Description("Find a song on youtube music using keywords!")
    fun SlashSender.youtubeMusic(keywords: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getGuildMusicManager(guild)
        bot.playerManager.loadItemOrdered(musicManager.player, "ytmsearch:$keywords", AudioLoaderResultHandler(event, musicManager, true))
    }

    @SubCommand("soundcloud")
    @Description("Find a song on soundcloud using keywords!")
    fun SlashSender.soundcloud(keywords: String) {
        event.deferReply().queue()
        if (!process(join = true, sameChannel = true, adminBypass = true, deferred = true)) {
            return
        }

        val guild = guild ?: return

        val musicManager = bot.getGuildMusicManager(guild)
        bot.playerManager.loadItemOrdered(musicManager.player, "scsearch:$keywords", AudioLoaderResultHandler(event, musicManager, true))
    }
}