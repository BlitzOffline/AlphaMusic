package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.settings.SettingsHandler
import me.mattstudios.config.SettingsManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AlphaMusic {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var settings: SettingsManager
        private set


    fun run() {
        val settingsHandler = SettingsHandler(this)
        settings = settingsHandler.fetchSettings()
    }
}