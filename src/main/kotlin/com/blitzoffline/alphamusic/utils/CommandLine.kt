package com.blitzoffline.alphamusic.utils

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class CommandLine(args: Array<String>) {
    private val cli = DefaultParser().parse(
        Options().apply {
            addOption(
                Option.builder("t").hasArg().argName("token").required().build()
            )
            addOption(
                Option.builder("e").hasArg().argName("email").required(false).build()
            )
            addOption(
                Option.builder("p").hasArg().argName("pass").required(false).build()
            )
        },
        args
    )

    fun fetchTokenFromFlag(): String {
        return cli.getOptionValue("t")
    }

    fun fetchEmailFromFlag(): String? {
        return cli.getOptionValue("e")
    }

    fun fetchPassFromFlag(): String? {
        return cli.getOptionValue("p")
    }
}