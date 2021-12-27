package com.blitzoffline.alphamusic.votes

import kotlin.math.ceil

class Votes(val type: VoteType, val votes: HashSet<String>) {
    fun getRequiredVotes(participants: Int): Int {
        return if (participants <= 4) 2 else ceil(participants * type.requiredPercentage).toInt()
    }
}