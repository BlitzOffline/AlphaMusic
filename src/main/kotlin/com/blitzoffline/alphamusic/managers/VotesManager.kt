package com.blitzoffline.alphamusic.managers

import com.blitzoffline.alphamusic.votes.VoteType
import com.blitzoffline.alphamusic.votes.Votes

class VotesManager {
    private val votes = HashMap<VoteType, Votes>()
    
    init {
        VoteType.values.forEach { type ->
            votes[type] = Votes(type, hashSetOf())
        }
    }

    fun clear() {
        votes.clear()
    }

    fun getVoteManager(type: VoteType): Votes? {
        return votes[type]
    }

    fun getVoteManager(name: String): Votes? {
        return votes[VoteType.getByName(name)]
    }
}