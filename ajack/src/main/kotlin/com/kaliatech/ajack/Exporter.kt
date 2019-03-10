/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import com.healthmarketscience.jackcess.Column
import com.healthmarketscience.jackcess.DatabaseBuilder
import com.healthmarketscience.jackcess.util.ExportFilter
import com.healthmarketscience.jackcess.util.ExportUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class Exporter {

    private val log: Logger = LoggerFactory.getLogger(Exporter::class.java)

    fun export(cmd: ExportCommand) {
        val db = DatabaseBuilder.open(File(cmd.mdbFilePath))

        log.info("Starting export...")
        val expBuilder = ExportUtil.Builder(db)
        if (cmd.tables.isEmpty()) {
            //jExporter.exportAll(File(cmd.destDir))
            cmd.tables = db.tableNames
        }

        for (tableName in cmd.tables) {
            val exportFile = Path.of(cmd.destDir, "$tableName.csv")
            if(!cmd.overwrite && Files.exists(exportFile)) {
                log.info("  {} - (file exists, skipping)", tableName)
                continue
            }

            expBuilder.setTableName(tableName)
            val table = db.getTable(tableName)
            log.info("  {} ({} rows)", tableName, table.rowCount)


            expBuilder.setFilter(object : ExportFilter {
                override fun filterColumns(columns: MutableList<Column>?): MutableList<Column> {
                    return columns!!
                }

                override fun filterRow(row: Array<Any>?): Array<Any> {
                    return row!!
                }

            })

            expBuilder.exportFile(exportFile.toFile())
        }

        log.info("complete.")
        //writeShellScripts(cmd, db;

    }
}
