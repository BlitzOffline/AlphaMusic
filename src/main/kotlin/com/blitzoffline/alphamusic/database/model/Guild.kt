package com.blitzoffline.alphamusic.database.model

import com.blitzoffline.alphamusic.database.table.Guilds
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

class Guild(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Guild>(Guilds)

    /**
     * The last known display name of this guild.
     */
    var guildName by Guilds.guildName

    /**
     * The [Instant] at which the bot joined this guild.
     */
    var joinedAt by Guilds.joinedAt

    /**
     * The [Instant] at which a change was made to this guild's settings.
     */
    var updatedAt by Guilds.updatedAt

    /**
     * The volume of the [AudioPlayer] in this guild.
     */
    var volume by Guilds.volume
    /**
     * If enabled, there will be an attempt to generate a new queue
     * based on the last played [AudioTrack] when there is no [AudioTrack]
     * left in the queue.
     */
    var radio by Guilds.radio
}