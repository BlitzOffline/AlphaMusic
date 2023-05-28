package com.blitzoffline.alphamusic.vote

import kotlin.math.ceil

class VoteHolder(val type: VoteType, val votes: HashSet<String>) {
    fun getRequiredVotes(participants: Int): Int {
        return if (participants <= 4) 2 else ceil(participants * type.requiredPercentage).toInt()
    }
}