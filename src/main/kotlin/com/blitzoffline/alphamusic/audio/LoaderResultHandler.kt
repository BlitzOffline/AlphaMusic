package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.utils.terminate
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class LoaderResultHandler(
    private val event: SlashCommandInteractionEvent?,
    private val musicManager: GuildMusicManager,
    private val trackService: TrackService,
    private val trackURL: String,
    private val jda: JDA,
    private val isRadio: Boolean,
    private val deferred: Boolean = false
) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack) {
        if (event != null) {
            track.userData = TrackMetadata(event.user)
        } else {
            track.userData = TrackMetadata(jda.selfUser)
        }
        trackService.audioItemCache.put(trackURL, track)

        if (musicManager.audioHandler.queue(track.makeClone())) {
            terminate(event, EmbedBuilder()
                .setAuthor("Added song to queue ♪", null, null)
                .setTitle(track.info.title, track.info.uri)
                .setThumbnail(track.info.artworkUrl)
                .build(), deferred = deferred)
        } else {
            terminate(event, "Queue is full. Could not add song.", deferred = deferred)
        }
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.tracks.isEmpty()) {
            return terminate(event, "Could not find any songs!", deferred = deferred)
        }

        if (musicManager.audioHandler.queue.remainingCapacity() == 0) {
            return terminate(event, "Queue is full. Could not add any more songs.", deferred = deferred)
        }

        trackService.audioItemCache.put(trackURL, playlist)

        if (isRadio && playlist.tracks.size == 1) {
            return terminate(event, "Could not find any songs based on the given identifier!", deferred = deferred)
        }

        if (playlist.isSearchResult || playlist.tracks.size == 1) {
            val track = playlist.tracks[0]
            if (event != null) {
                track.userData = TrackMetadata(event.user)
            } else {
                track.userData = TrackMetadata(jda.selfUser)
            }

            if (musicManager.audioHandler.queue(track.makeClone())) {
                return terminate(event, EmbedBuilder()
                    .setAuthor("Added song to queue ♪", null, null)
                    .setTitle(track.info.title, track.info.uri)
                    .setThumbnail(track.info.artworkUrl)
                    .build(), deferred = deferred)
            }

            return terminate(event, "Something went wrong while adding song to queue.", deferred = deferred)
        }

        var count = 0
        val tracks = playlist.tracks
        tracks.removeFirst()
        for (track in tracks) {
            if (event != null) {
                track.userData = TrackMetadata(event.user)
            } else {
                track.userData = TrackMetadata(jda.selfUser)
            }
            if (musicManager.audioHandler.queue(track.makeClone())) {
                count++
            }
        }

        if (count == 0) {
            return terminate(event, "Something went wrong while adding songs to queue.", deferred = deferred)
        }

        if (count != playlist.tracks.size) {
            return terminate(event, "Could only add $count ${if (count == 1) "song" else "songs"} to the queue because it is full!", deferred =deferred)
        }

        terminate(event, "Added $count ${if (count == 1) "song" else "songs"} to the queue.", deferred = deferred)
    }

    override fun noMatches() {
        terminate(event, "Could not find any songs based on the given identifier!", deferred = deferred)
    }

    override fun loadFailed(throwable: FriendlyException) {
        val message = throwable.message

        if (throwable.severity == FriendlyException.Severity.COMMON && message != null) {
            terminate(event, message, deferred = deferred)
        } else {
            terminate(event, "Something went wrong while loading the song!", deferred = deferred)
        }
    }

    private fun terminate(event: SlashCommandInteractionEvent?, message: String, deferred: Boolean) {
        if (event == null) return
        event.terminate(message, false, deferred)
    }

    private fun terminate(event: SlashCommandInteractionEvent?, message: MessageEmbed, deferred: Boolean) {
        if (event == null) return
        event.terminate(message, false, deferred)
    }
}