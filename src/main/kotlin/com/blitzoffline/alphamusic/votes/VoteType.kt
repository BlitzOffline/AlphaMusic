package com.blitzoffline.alphamusic.votes

enum class VoteType {
    CLEAR,
    LEAVE,
    LOOP,
    PAUSE,
    REMOVE_DUPES,
    REPLAY,
    RESUME,
    SHUFFLE,
    SKIP,
    STOP;

    companion object {
        val values = values()

        fun getByName(name: String): VoteType? {
            return values.firstOrNull { it.name.equals(name, true) }
        }
    }
}