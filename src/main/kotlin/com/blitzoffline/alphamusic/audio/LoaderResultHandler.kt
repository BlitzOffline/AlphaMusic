package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.utils.terminate
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class LoaderResultHandler(
    private val event: SlashCommandEvent,
    private val musicManager: GuildMusicManager,
    private val trackService: TrackService,
    private val trackURL: String,
    private val deferred: Boolean = false
) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack) {
        track.userData = TrackMetadata(event.user)
        trackService.audioItemCache.put(trackURL, track)

        if (musicManager.audioHandler.queue(track.makeClone())) {
            event.terminate("Added song to queue.", deferred = deferred)
        } else {
            event.terminate("Queue is full. Could not add song.", deferred = deferred)
        }
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.tracks.isEmpty()) {
            return event.terminate("Could not find any songs!", deferred = deferred)
        }

        if (musicManager.audioHandler.queue.remainingCapacity() == 0) {
            return event.terminate("Queue is full. Could not add any more songs.", deferred = deferred)
        }

        trackService.audioItemCache.put(trackURL, playlist)

        if (playlist.isSearchResult || playlist.tracks.size == 1) {
            val track = playlist.tracks[0]
            track.userData = TrackMetadata(event.user)

            if (musicManager.audioHandler.queue(track.makeClone())) {
                return event.terminate("Added song to queue.", deferred = deferred)
            }

            return event.terminate("Something went wrong while adding song to queue.", deferred = deferred)
        }

        var count = 0
        for (track in playlist.tracks) {
            track.userData = TrackMetadata(event.user)
            if (musicManager.audioHandler.queue(track.makeClone())) {
                count++
            }
        }

        if (count == 0) {
            return event.terminate("Something went wrong while adding songs to queue.", deferred = deferred)
        }

        if (count != playlist.tracks.size) {
            return event.terminate("Could only add $count ${if (count == 1) "song" else "songs"} to the queue because it is full!", deferred =deferred)
        }

        event.terminate("Added $count ${if (count == 1) "song" else "songs"} to the queue.", deferred = deferred)
    }

    override fun noMatches() {
        event.terminate("Could not find any songs based on the given identifier!", deferred = deferred)
    }

    override fun loadFailed(throwable: FriendlyException) {
        val message = throwable.message

        if (throwable.severity == FriendlyException.Severity.COMMON && message != null) {
            event.terminate(message, deferred = deferred)
        } else {
            event.terminate("Something went wrong while loading the song!", deferred = deferred)
        }
    }
}