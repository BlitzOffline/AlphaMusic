package com.blitzoffline.alphamusic.votes

class VoteManager {
    private val voteHolder = HashMap<VoteType, VoteHolder>()
    
    init {
        VoteType.values.forEach { type ->
            voteHolder[type] = VoteHolder(type, hashSetOf())
        }
    }

    fun clear() {
        voteHolder.clear()
    }

    fun getVoteManager(type: VoteType): VoteHolder? {
        return voteHolder[type]
    }

    fun getVoteManager(name: String): VoteHolder? {
        return voteHolder[VoteType.getByName(name)]
    }
}