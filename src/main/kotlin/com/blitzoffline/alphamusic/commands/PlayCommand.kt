package com.blitzoffline.alphamusic.commands

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.audio.AudioLoaderResultHandler
import com.blitzoffline.alphamusic.utils.terminate
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Description
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.slash.sender.SlashSender
import net.dv8tion.jda.api.Permission

@Command("play")
@Description("Play a song!")
class PlayCommand(private val bot: AlphaMusic) {

    @SubCommand("link")
    @Description("Find a song using a link!")
    fun SlashSender.link(link: String) {
        processSong(link)
    }

    @SubCommand("youtube")
    @Description("Find a song on youtube using keywords!")
    fun SlashSender.youtube(keywords: String) {
        processSong("ytsearch:$keywords")
    }

    @SubCommand("youtube-music")
    @Description("Find a song on youtube music using keywords!")
    fun SlashSender.youtubeMusic(keywords: String) {
        processSong("ytmsearch:$keywords")
    }

    @SubCommand("soundcloud")
    @Description("Find a song on soundcloud using keywords!")
    fun SlashSender.soundcloud(keywords: String) {
        processSong("scsearch:$keywords")
    }

    private fun SlashSender.processSong(search: String) {
        event.deferReply().queue()

        val guild = guild ?: return event.terminate("This command can only be used in a guild!", ephemeral = false, deferred = true)
        val member = member ?: return event.terminate("This command can only be used in a guild!", ephemeral = false, deferred = true)

        val alphaMusic = guild.selfMember
        val memberVC = member.voiceState?.channel
        val alphaMusicVC = alphaMusic.voiceState?.channel

        if (alphaMusicVC == null) {
            if (kotlin.runCatching { guild.audioManager.openAudioConnection(memberVC) }.isFailure) {
                return event.terminate("Something went wrong while trying to connect to your voice channel!", deferred = true)
            }
        }

        if (alphaMusicVC != memberVC && !member.hasPermission(Permission.ADMINISTRATOR)) {
            return event.terminate("You need to be in the same Voice Channel as the bot to do this!", deferred = true)
        }

        val musicManager = bot.getGuildMusicManager(guild)
        bot.playerManager.loadItemOrdered(musicManager.player, search, AudioLoaderResultHandler(event, musicManager, true))
    }
}