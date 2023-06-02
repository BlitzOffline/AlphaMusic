package com.blitzoffline.alphamusic.holder

import com.blitzoffline.alphamusic.database.table.Guilds
import com.blitzoffline.alphamusic.utils.extension.toTimeUnit
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import net.dv8tion.jda.api.JDA
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.concurrent.Executors

class CachedGuildHolder(private val jda: JDA) {
    private val delay = System.getenv("ALPHAMUSIC_MYSQL_DELAY")?.toLong() ?: 30
    private val timeUnit = System.getenv("ALPHAMUSIC_MYSQL_TIMEUNIT").toTimeUnit()

    private val scheduler = Executors.newScheduledThreadPool(1)

    private val cacheLoader = object : CacheLoader<String, CachedGuild>() {
        override fun load(key: String): CachedGuild {
            return getByIdOrCreate(key)
        }
    }

    private val cache = CacheBuilder
        .newBuilder()
        .build(cacheLoader)

    init {
        scheduler.scheduleAtFixedRate(
            { save() },
            delay,
            delay,
            timeUnit
        )
    }

    fun radio(id: String): Boolean {
        return get(id).radio
    }

    fun volume(id: String): Int {
        return get(id).volume
    }

    fun joinedAt(id: String): Instant {
        return get(id).joinedAt
    }

    fun updatedAt(id: String): Instant {
        return get(id).updatedAt
    }

    fun loop(id: String): Boolean {
        return get(id).loop
    }

    fun replay(id: String): Boolean {
        return get(id).replay
    }

    fun setRadio(id: String, radio: Boolean) {
        cache.put(id, get(id).copy(radio = radio, updatedAt = Instant.now()))
    }

    fun setVolume(id: String, volume: Int) {
        cache.put(id, get(id).copy(volume = volume, updatedAt = Instant.now()))
    }

    fun setLoop(id: String, loop: Boolean) {
        cache.put(id, get(id).copy(loop = loop))
    }

    fun setReplay(id: String, replay: Boolean) {
        cache.put(id, get(id).copy(replay = replay))
    }

    fun get(id: String): CachedGuild {
        return cache.get(id)
    }

    fun save() {
        cache.asMap().forEach { (key, value) ->
            updateById(key, value)
        }
    }

    private fun getByIdOrCreate(id: String): CachedGuild {
        return transaction {
            val guild = Guilds.findByIdOrCreate(id) {
                this.joinedAt = jda.getGuildById(id)?.retrieveMemberById(jda.selfUser.id)?.complete()?.timeJoined?.toInstant() ?: Instant.now()
            }
            return@transaction CachedGuild(guild.radio, guild.volume, guild.joinedAt, guild.updatedAt)
        }
    }

    private fun updateById(key: String, oldValue: CachedGuild): CachedGuild {
        return transaction {
            Guilds.update({ Guilds.id eq key }) { updateStatement ->
                updateStatement[radio] = oldValue.radio
                updateStatement[volume] = oldValue.volume
                updateStatement[joinedAt] = oldValue.joinedAt
                updateStatement[updatedAt] = oldValue.updatedAt
            }

            return@transaction oldValue
        }
    }
}