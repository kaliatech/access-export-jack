/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech

import com.kaliatech.ajack.ExportCommand
import picocli.CommandLine

fun main(args: Array<String>) {
    val exportCmd = ExportCommand()
    CommandLine.run(exportCmd, *args)
}
