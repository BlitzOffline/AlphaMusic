package com.blitzoffline.alphamusic.utils.extension

import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.slf4j.Logger
import org.slf4j.event.Level
import java.io.PrintWriter
import java.io.StringWriter

fun HelpFormatter.printHelp(
    command: String,
    options: Options,
    logger: Logger,
    level: Level = Level.INFO
) {
    StringWriter().use { stringWriter ->
        PrintWriter(stringWriter).use { printWriter ->
            this.printHelp(printWriter, this.width, command, null, options, this.leftPadding, this.descPadding, null, true)
            printWriter.flush()
            logger.log(level, stringWriter.toString())
        }
    }
}