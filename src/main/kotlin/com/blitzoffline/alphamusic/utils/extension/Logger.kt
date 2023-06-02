package com.blitzoffline.alphamusic.utils.extension

import org.slf4j.Logger
import org.slf4j.event.Level

fun Logger.log(level: Level, message: String) {
    when (level) {
        Level.TRACE -> trace(message)
        Level.DEBUG -> debug(message)
        Level.INFO -> info(message)
        Level.WARN -> warn(message)
        Level.ERROR -> error(message)
    }
}

fun Logger.log(level: Level, message: String, throwable: Throwable) {
    when (level) {
        Level.TRACE -> trace(message, throwable)
        Level.DEBUG -> debug(message, throwable)
        Level.INFO -> info(message, throwable)
        Level.WARN -> warn(message, throwable)
        Level.ERROR -> error(message, throwable)
    }
}