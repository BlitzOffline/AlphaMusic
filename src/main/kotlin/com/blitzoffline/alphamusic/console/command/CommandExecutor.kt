package com.blitzoffline.alphamusic.console.command

/**
 * Simple command executor interface that allows execution of a command.
 */
interface CommandExecutor {
    /**
     * Execute the command. Arguments can optionally be passed in the form of a string. It is up to the command to parse them.
     * @param arguments Arguments to use when executing the command.
     */
    fun execute(arguments: String? = null)
}