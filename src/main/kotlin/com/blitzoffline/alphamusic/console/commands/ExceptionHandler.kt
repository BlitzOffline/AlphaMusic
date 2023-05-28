package com.blitzoffline.alphamusic.console.commands

import com.blitzoffline.alphamusic.utils.log
import org.apache.commons.cli.*
import org.slf4j.Logger
import org.slf4j.event.Level

/**
 * Handles exceptions thrown by [CommandParser]s
 *
 * @param exception The exception thrown
 * @param command The command that was executed
 * @param logger The logger to log to
 * @param level The level to log at
 * @return Whether the exception was handled or not
 */
fun handleException(exception: ParseException, command: String, options: Options, logger: Logger, level: Level): Boolean {
    when (exception) {
        is AlreadySelectedException -> {
            val badOption = exception.option.opt
            val otherOptions = exception.optionGroup.options.map { it.opt }.filter { it != badOption }.map { it }
            logger.log(level, "-$badOption can not be used together with: $otherOptions")
            logger.debug("Something went wrong while executing command: $command", exception)
            return true
        }

        is MissingArgumentException -> {
            logger.log(level, "Missing value for option: ${exception.option.opt}")
            logger.debug("Something went wrong while executing command: $command", exception)
            return true
        }

        is MissingOptionException -> {
            val first = exception.missingOptions.first()
            if (first == null) {
                logger.log(level, "Something went wrong while executing command: $command")
                logger.debug("Something went wrong while executing command: $command", exception)
                return true
            }

            if (first is OptionGroup) {
                val missingOptions = first.options.map { it.opt }

                logger.log(level, "Missing required option. You must specify one of the following options: $missingOptions")
                logger.debug("Something went wrong while executing command: $command", exception)
                return true
            }

            if (first is Option) {
                logger.log(level, "Missing required option. You must specify the following option: ${first.opt}")
                logger.debug("Something went wrong while executing command: $command", exception)
                return true
            }

            if (first is String) {
                val option = if (options.hasShortOption(first)) { options.getOption(first) } else { null }

                if (option != null) {
                    logger.log(level, "Missing required option. You must specify the following option: ${option.opt}")
                    logger.debug("Something went wrong while executing command: $command", exception)
                    return true
                }

                logger.log(level, "Missing required option. You must specify the following option: -$first")
                logger.debug("Something went wrong while executing command: $command", exception)
                return true
            }

            return false
        }

        is UnrecognizedOptionException -> {
            logger.log(level, "Option not found: ${exception.option}.")
            logger.debug("Something went wrong while executing command: $command", exception)
            return true
        }

        else -> {
            return false
        }
    }
}