package com.blitzoffline.alphamusic.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun SlashCommandInteractionEvent.terminate(reason: String = "Something went wrong!", ephemeral: Boolean = false, deferred: Boolean = false) {
    if (deferred) {
        return this.interaction.hook.editOriginal(reason).queue()
    }
    this.reply(reason).setEphemeral(ephemeral).queue()
}

fun SlashCommandInteractionEvent.terminate(reason: MessageEmbed = EmbedBuilder().setDescription("Something went wrong!").build(), ephemeral: Boolean = false, deferred: Boolean = false) {
    if (deferred) {
        return this.interaction.hook.editOriginalEmbeds(reason).queue()
    }
    this.replyEmbeds(reason).setEphemeral(ephemeral).queue()
}