/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import com.healthmarketscience.jackcess.DatabaseBuilder
import com.healthmarketscience.jackcess.util.ExportUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Exporter {

    private val log: Logger = LoggerFactory.getLogger(Exporter::class.java)

    fun export(cmd: ExportCommand) {
        val db = DatabaseBuilder.open(File(cmd.mdbFilePath))

        log.info("Starting export...")
        val jExporter = ExportUtil.Builder(db)
        jExporter.exportAll(File(cmd.destDir))
        log.info("complete.")
    }
}
