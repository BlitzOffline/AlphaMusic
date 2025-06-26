package com.blitzoffline.alphamusic.console

import com.blitzoffline.alphamusic.console.command.Command
import com.blitzoffline.alphamusic.console.command.AppConsoleCommand
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger

class ConsoleApplication(jda: JDA, private val logger: Logger) {
    private val commands: Set<Command> = setOf(
        AppConsoleCommand("app", jda, logger)
    )

    fun run() {
        logger.debug("Successfully started console handler...")
        while (true) {
            val input = readlnOrNull()
            if (input.isNullOrBlank()) continue

            val splitInput = input.split(" ", limit = 2)

            val command = commands.firstOrNull { it.name.equals(splitInput[0], true) }
            if (command == null) {
                logger.warn("Unknown command: ${splitInput[0]}. Available commands: ${commands.joinToString(", ") { it.name }}")
                continue
            }

            command.execute(splitInput.getOrNull(1))
        }
    }
}