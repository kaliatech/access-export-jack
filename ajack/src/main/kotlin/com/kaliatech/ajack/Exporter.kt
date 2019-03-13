/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import com.healthmarketscience.jackcess.Database
import com.healthmarketscience.jackcess.DatabaseBuilder
import com.healthmarketscience.jackcess.util.ExportUtil
import com.kaliatech.ajack.ddl.DdlGeneratorPostgres
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path

class Exporter {

    private val log: Logger = LoggerFactory.getLogger(Exporter::class.java)

    fun export(cmd: ExportCommand) {
        val db = DatabaseBuilder.open(File(cmd.mdbFilePath))

        log.info("Starting export...")

        // If no specific table(s) given, collect all tables
        if (cmd.tables.isEmpty()) {
            cmd.tables = db.tableNames
        }

        // Export DDL
        exportSchemaDdl(cmd, db)

        // Export data
        exportData(cmd, db)

        // Export shell scripts
        exportCreateScript(cmd)
        exportLoadScript(cmd)

        log.info("complete.")
        //writeShellScripts(cmd, db;

    }

    private fun exportCreateScript(cmd: ExportCommand) {
        var scriptFile = createFileIfNotExists("_create.sh", cmd, false) ?: return


        val schemaFilename = "schema.ddl"
        val prefixedSchemaFilename =
            if (StringUtils.isNotBlank(cmd.prefix)) cmd.prefix + schemaFilename else schemaFilename

        PrintWriter(scriptFile.toFile()).use { out ->
            out.append("#!/usr/bin/env bash\n")
            out.append("# Useful environment variables:\n")
            out.append(
                """
                #   PGDATABASE
                #   PGHOST
                #   PGPORT
                #   PGUSER
                """.trimIndent()
            )
            out.append("\n")
            out.append("# And ~/.pgpass file to avoid regularly having to type in passwords\n")
            out.append("\n")
            out.append("psql -f $prefixedSchemaFilename")
        }

        log.info("  {} ", scriptFile.fileName)
    }

    private fun exportLoadScript(cmd: ExportCommand) {
        var scriptFile = createFileIfNotExists("_load.sh", cmd, false) ?: return

        PrintWriter(scriptFile.toFile()).use { out ->
            out.append("#!/usr/bin/env bash\n")
            out.append("\n")
            out.append("psql \\\n")
            for (tableName in cmd.tables) {

                val prefixedTablename =
                    if (StringUtils.isNotBlank(cmd.prefix)) cmd.prefix + tableName else tableName
                val modTablename = if (cmd.caseSensitive) prefixedTablename else prefixedTablename.toLowerCase()
                val modFilename = "$modTablename.csv"

                out.append(" -c \"\\copy $modTablename FROM './$modFilename' WITH (FORMAT csv)\"")
                if (tableName != cmd.tables.last()) {
                    out.append(" \\\n")
                }
            }
        }

        log.info("  {} ", scriptFile.fileName)
    }

    private fun exportData(cmd: ExportCommand, db: Database) {

        val expBuilder = ExportUtil.Builder(db)

        for (tableName in cmd.tables) {
            val exportFile = createFileIfNotExists("$tableName.csv", cmd, true) ?: return

            expBuilder.setTableName(tableName)
            val table = db.getTable(tableName)
            log.info("  {} ({} rows)", exportFile.fileName, table.rowCount)


            //TBD if needed:
//            expBuilder.setFilter(object : ExportFilter {
//                override fun filterColumns(columns: MutableList<Column>?): MutableList<Column> {
//                    return columns!!
//                }
//
//                override fun filterRow(row: Array<Any>?): Array<Any> {
//                    return row!!
//                }
//
//            })

            expBuilder.exportFile(exportFile.toFile())
        }
    }

    private fun createFileIfNotExists(filename: String, cmd: ExportCommand, applyPrefix: Boolean): Path? {
        val prefixedFilename =
            if (applyPrefix && StringUtils.isNotBlank(cmd.prefix)) cmd.prefix + filename else filename
        val caseFilename = if (cmd.caseSensitive) prefixedFilename else prefixedFilename.toLowerCase()
        val filePath = Path.of(cmd.destDir, caseFilename)
        if (Files.exists(filePath) && !cmd.overwrite) {
            log.info("  {} - (file exists, skipping)", filePath)
            return null
        }

        return filePath
    }

    private fun exportSchemaDdl(cmd: ExportCommand, db: Database) {
        // TODO: Eventually factory according to cmd.format
        val ddlGenerator = DdlGeneratorPostgres(db)
        val schemaFilename = if (StringUtils.isNotBlank(cmd.prefix)) cmd.prefix + "schema.ddl" else "schema.ddl"
        val schemaExportFile = Path.of(cmd.destDir, schemaFilename)
        if (!cmd.overwrite && Files.exists(schemaExportFile)) {
            log.info("  {} - (file exists, skipping)", schemaExportFile.fileName)
        } else {
            val schema = ddlGenerator.getSchema(cmd)
            PrintWriter(schemaExportFile.toFile()).use { out -> out.write(schema) }
            log.info("  {} ({} tables)", schemaExportFile.fileName, cmd.tables.size)
        }
    }
}
