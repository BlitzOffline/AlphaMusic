package com.blitzoffline.alphamusic.track

import net.dv8tion.jda.api.entities.User

class TrackMetadata(user: User) {
    val data = UserData(user.id, user.name, user.discriminator, user.effectiveAvatarUrl)

    data class UserData(val id: String, val name: String, val discriminator: String, val avatar: String)
}