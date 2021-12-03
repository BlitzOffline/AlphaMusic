package com.blitzoffline.alphamusic.audio

import com.blitzoffline.alphamusic.utils.terminate
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class AudioLoaderResultHandler(private val event: SlashCommandEvent, private val musicManager: GuildMusicManager, private val deferred: Boolean = false) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack) {
        track.userData = TrackMetadata(event.user)
        if (musicManager.audioHandler.queue(track)) {
            event.terminate("Added song to queue.", deferred = deferred)
        } else {
            event.terminate("Queue is full. Could not add song.", deferred = deferred)
        }
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (musicManager.audioHandler.queue.remainingCapacity() == 0) {
            return event.terminate("Queue is full. Could not add any more songs.", deferred = deferred)
        }

        if (playlist.tracks.isEmpty()) {
            return event.terminate("Could not find any songs!", deferred = deferred)
        }

        var count = 0
        for (track in playlist.tracks) {
            track.userData = TrackMetadata(event.user)
            if (musicManager.audioHandler.queue(track)) {
                count ++
            }
        }

        if (count == 1) {
            event.terminate("Added $count song to the queue.", deferred = deferred)
        } else {
            event.terminate("Added $count songs to the queue.", deferred = deferred)
        }
    }

    override fun noMatches() {
        event.terminate("Could not find any songs!", deferred = deferred)
    }

    override fun loadFailed(throwable: FriendlyException) {
        val message = throwable.message

        if (throwable.severity == FriendlyException.Severity.COMMON && message != null) {
            event.terminate(message, deferred = deferred)
        } else {
            event.terminate("Something went wrong while loading!", deferred = deferred)
        }
    }
}