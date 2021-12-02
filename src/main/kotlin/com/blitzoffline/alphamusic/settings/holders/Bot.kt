package com.blitzoffline.alphamusic.settings.holders

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Bot : SettingsHolder {
    @Path("bot.token")
    val TOKEN = Property.create("bot-token")
}