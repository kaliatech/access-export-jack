/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech

import com.kaliatech.ajack.ExportCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine

fun main(args: Array<String>) {
    val topLevelClass = object : Any() {}.javaClass.enclosingClass
    val log: Logger = LoggerFactory.getLogger(topLevelClass)

    try {
        val exportCmd = ExportCommand()
        CommandLine.run(exportCmd, *args)
    } catch (e: Exception) {
        log.error("Uncaught error during export.", e)
    }
}
