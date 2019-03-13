/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


// TODO: inject version at build time
@Command(name = "ajack", version = ["ajack v0.0.0"], footer = ["Copyright(c) 2019"])
class ExportCommand : Runnable {

    private val log: Logger = LoggerFactory.getLogger(ExportCommand::class.java)

    private val dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")

    @Option(
        names = ["-h", "--help"], usageHelp = true,
        description = ["print this help and exit"]
    )
    private var helpRequested: Boolean = false

    @Option(
        names = ["-v", "--version"], versionHelp = true,
        description = ["print version and exit"]
    )
    private var versionRequested: Boolean = false


    @Option(
        names = ["-d", "--destination"],
        paramLabel = "<dir>",
        description = ["Output directory. Default: ./export-yyyyMMdd-HHmmss"]
    )
    var destDir: String = "./export-" + dtf.format(ZonedDateTime.now())

    @Option(
        names = ["-o", "--overwrite"],
        description = ["Overwrite any existing files."]
    )
    var overwrite: Boolean = false

    @Option(
        names = ["-f", "--format"],
        description = ["Output format. Valid values: \${COMPLETION-CANDIDATES}"]
    )
    var format: ExportFormat = ExportFormat.POSTGRES_CSV

    @Option(
        names = ["-cs", "--case-sensitive"],
        description = ["Case sensitive DDL. Default false."]
    )
    var caseSensitive: Boolean = false

    @Option(
        names = ["-p", "--prefix"],
        description = ["Table name prefix."]
    )
    var prefix: String = ""

    @Option(
        names = ["-t", "--table"],
        paramLabel = "table",
        split = ",",
        description = ["Specific tables to export. Default: all"]
    )
    var tables: Set<String> = LinkedHashSet()

    @Parameters(index = "0", paramLabel = "<mdb-file>", description = ["mdb file"])
    var mdbFilePath: String? = null

    override fun run() {

        if (!verify(this)) {
            System.exit(-1)
        }

        try {
            val exporter = Exporter()
            exporter.export(this)
        } catch (e: Exception) {
            log.error("Uncaught error during export.", e)
            System.exit(-1)
        }

    }
}
