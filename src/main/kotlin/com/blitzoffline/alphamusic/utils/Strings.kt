package com.blitzoffline.alphamusic.utils

import java.time.Duration
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

fun fetchTokenFromFlag(args: Array<String>): String {
    val cli = DefaultParser().parse(
        Options().apply {
            addOption(Option.builder("t").hasArg().argName("token").required().build())
        },
        args
    )

    return cli.getOptionValue("t")
}


fun formatHMS(duration: Duration): String {
    val builder = StringBuilder()

    val hours = duration.toHours()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()

    if (hours > 0) builder.append(hours)

    if (minutes > 0) {
        if (hours > 0) builder.append(":")
        if (minutes > 9) {
            builder.append(minutes)
        } else {
            if (hours > 0) {
                builder.append("0$minutes")
            } else {
                builder.append(minutes)
            }
        }
    } else {
        if (hours > 0) {
            builder.append(":00")
        } else {
            builder.append("00")
        }
    }

    if (seconds > 0) {
        builder.append(":")
        if (seconds > 9) {
            builder.append(seconds)
        } else {
            builder.append("0$seconds")
        }
    } else {
        builder.append(":00")
    }

    return builder.toString()
}