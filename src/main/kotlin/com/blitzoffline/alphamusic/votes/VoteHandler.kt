package com.blitzoffline.alphamusic.votes

class VoteHandler {
    private val guildVoteManager = HashMap<VoteType, GuildVoteManager>()
    
    init {
        VoteType.values.forEach { type ->
            guildVoteManager[type] = GuildVoteManager(type, hashSetOf())
        }
    }

    fun clear() {
        guildVoteManager.clear()
    }

    fun getVoteManager(type: VoteType): GuildVoteManager? {
        return guildVoteManager[type]
    }

    fun getVoteManager(name: String): GuildVoteManager? {
        return guildVoteManager[VoteType.getByName(name)]
    }
}