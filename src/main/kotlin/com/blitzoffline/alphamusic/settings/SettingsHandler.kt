package com.blitzoffline.alphamusic.settings

import com.blitzoffline.alphamusic.AlphaMusic
import com.blitzoffline.alphamusic.settings.holders.Bot
import java.io.File
import me.mattstudios.config.SettingsManager

class SettingsHandler(private val bot: AlphaMusic) {
    fun fetchSettings() = SettingsManager
        .from(fetchSettingsFile("settings.yml"))
        .configurationData(
            Bot::class.java
        )
        .create()

    private fun fetchSettingsFile(name: String): File {
        val file = File("$dir/$name")
        if (!file.exists()) {
            saveDefaultFile(name)
        }
        return file
    }

    private fun saveDefaultFile(name: String) {
        val file = File("$dir/$name")
        if (file.exists()) {
            return
        }

        val stream = AlphaMusic::class.java.classLoader.getResourceAsStream(name)
            ?: return bot.logger.error("Could not save the default $name file!")

        file.writeBytes(stream.readAllBytes())
    }

    private val dir = File(AlphaMusic::class.java.protectionDomain.codeSource.location.path.substringBeforeLast("/") + "/AlphaMusic/")

    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
}