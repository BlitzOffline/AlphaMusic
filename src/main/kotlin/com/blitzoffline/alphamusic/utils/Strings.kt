package com.blitzoffline.alphamusic.utils

import java.time.Duration

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

fun formatHMSDouble(progress: Duration, duration: Duration): String {
    val progressBuilder = StringBuilder()
    val durationBuilder = StringBuilder()

    val progressHours = progress.toHours()
    val progressMinutes = progress.toMinutesPart()
    val progressSeconds = progress.toSecondsPart()

    val durationHours = duration.toHours()
    val durationMinutes = duration.toMinutesPart()
    val durationSeconds = duration.toSecondsPart()

    if (durationHours > 0) durationBuilder.append(durationHours)

    if (durationMinutes > 0) {
        if (durationHours > 0) {
            durationBuilder.append(":")
        }
        if (durationMinutes > 9) {
            durationBuilder.append(durationMinutes)
        } else {
            durationBuilder.append("0$durationMinutes")
        }
    } else {
        if (durationHours > 0) {
            durationBuilder.append(":00")
        } else {
            durationBuilder.append("00")
        }
    }

    if (durationSeconds > 0) {
        durationBuilder.append(":")

        if (durationSeconds > 9) {
            durationBuilder.append(durationSeconds)
        } else {
            durationBuilder.append("0$durationSeconds")
        }
    } else {
        durationBuilder.append(":00")
    }


    if (progressHours > 0) progressBuilder.append(progressHours)

    if (progressMinutes > 0) {
        if (progressHours > 0) progressBuilder.append(":")

        if (progressMinutes > 9) {
            progressBuilder.append(progressMinutes)
        } else {
            progressBuilder.append("0$progressMinutes")
        }
    } else {
        if (progressHours > 0) {
            progressBuilder.append(":00")
        } else {
            progressBuilder.append("00")
        }
    }

    if (progressSeconds > 0) {
        progressBuilder.append(":")

        if (progressSeconds > 9) {
            progressBuilder.append(progressSeconds)
        } else {
            progressBuilder.append("0$progressSeconds")
        }
    } else {
        progressBuilder.append(":00")
    }


    return  "$progressBuilder / $durationBuilder"
}

fun progressBar(progress: Long, duration: Long): String {
    val builder = StringBuilder()

    val partValue = duration / 30
    val position = (progress / partValue).toInt()

    if (position > 1) repeat(position-1) { builder.append("▬") }
    builder.append("\uD83D\uDD18")
    if (position < 29) repeat(30-position-1) { builder.append("▬") }

    return builder.toString()
}