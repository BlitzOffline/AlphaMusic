package com.blitzoffline.alphamusic.vote

enum class VoteType(val requiredPercentage: Double) {
    CLEAR(0.3),
    LEAVE(0.3),
    LOOP(0.3),
    RADIO(0.3),
    REMOVE_DUPES(0.3),
    REPLAY(0.3),
    SHUFFLE(0.3),
    SKIP(0.3),
    STOP(0.3);

    companion object {
        val values = values()

        fun getByName(name: String): VoteType? {
            return values.firstOrNull { it.name.equals(name, true) }
        }
    }
}