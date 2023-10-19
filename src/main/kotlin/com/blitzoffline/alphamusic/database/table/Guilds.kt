package com.blitzoffline.alphamusic.database.table

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import com.blitzoffline.alphamusic.database.model.Guild as GuildModel

object Guilds : IdTable<String>("guilds") {
    fun findByIdOrCreate(id: String, init: GuildModel.() -> Unit = {}): GuildModel {
        return GuildModel.findById(id) ?: GuildModel.new(id) { init() }
    }

    override val id = varchar("id", 64).entityId()
    val guildName = varchar("guild_name", 100).nullable().default(null)
    val radio = bool("radio").default(false)
    val volume = integer("volume").default(100)
    val joinedAt = timestamp("joined_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}