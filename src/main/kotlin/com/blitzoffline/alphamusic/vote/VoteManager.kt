package com.blitzoffline.alphamusic.vote

class VoteManager {
    private val voteHolders = HashMap<VoteType, VoteHolder>()
    
    init {
        VoteType.values.forEach { type ->
            voteHolders[type] = VoteHolder(type, hashSetOf())
        }
    }

    fun clear() {
        voteHolders.clear()
    }

    fun getVoteManager(type: VoteType): VoteHolder? {
        return voteHolders[type]
    }

    fun getVoteManager(name: String): VoteHolder? {
        return voteHolders[VoteType.getByName(name)]
    }
}