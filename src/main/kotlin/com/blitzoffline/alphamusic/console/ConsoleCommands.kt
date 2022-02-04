package com.blitzoffline.alphamusic.console

import com.blitzoffline.alphamusic.AlphaMusic
import kotlin.system.exitProcess
import net.dv8tion.jda.api.JDA

class ConsoleCommands(private val bot: AlphaMusic) {
    fun run() {
        while(true) {
            val command = readLine() ?: continue

            when {
                command.equals("stop", true) || command.equals("shutdown", true) -> {
                    bot.logger.info("Closing JDA instance...")
                    bot.jda.guilds.forEach { it.updateCommands().queue() }
                    bot.jda.updateCommands()
                    bot.jda.shutdown()
                    while (true) {
                        if (bot.jda.status != JDA.Status.SHUTDOWN) continue
                        bot.logger.info("Closed JDA instance successfully! Stopping application...")
                        exitProcess(0)
                    }
                }
                command.equals("help", true) -> {
                    bot.logger.info("Available commands:")
                    bot.logger.info("")
                    bot.logger.info("* help - Shows this help menu")
                    bot.logger.info("* stop - Stops the application")
                }
                else -> {
                    if (command.isEmpty()) continue
                    bot.logger.info("Command not found: $command. Type \"help\" to list available commands.")
                }
            }
        }
    }
}