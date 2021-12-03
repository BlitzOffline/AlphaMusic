package com.blitzoffline.alphamusic.utils

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

fun SlashCommandEvent.terminate(reason: String = "Something went wrong!", ephemeral: Boolean = true, deferred: Boolean = false) {
    if (deferred) {
        return this.interaction.hook.editOriginal(reason).queue()
    }
    this.reply(reason).setEphemeral(ephemeral).queue()
}