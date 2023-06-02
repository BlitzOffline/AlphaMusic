package com.blitzoffline.alphamusic.holder

import java.time.Instant

data class CachedGuild(
    val radio: Boolean,
    val volume: Int,
    val joinedAt: Instant,
    val updatedAt: Instant,
    val loop: Boolean = false,
    val replay: Boolean = false
)