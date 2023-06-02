package com.blitzoffline.alphamusic.utils.extension

import java.util.concurrent.TimeUnit

private val timeUnits = TimeUnit.values().associateBy { it.name.lowercase() }

fun String?.toTimeUnit(default: TimeUnit = TimeUnit.MINUTES): TimeUnit {
    val name = this?.lowercase() ?: return default
    return timeUnits[name] ?: default
}