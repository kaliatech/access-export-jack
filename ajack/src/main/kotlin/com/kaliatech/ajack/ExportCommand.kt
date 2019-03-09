/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import picocli.CommandLine.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


// TODO: inject version at build time
@Command(name = "ajack", version = ["ajack v0.0.0"], footer = ["Copyright(c) 2019"])
class ExportCommand : Runnable {

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
        names = ["-f", "--format"],
        description = ["Output format. Valid values: \${COMPLETION-CANDIDATES}"]
    )
    var format: ExportFormat = ExportFormat.POSTGRES_CSV

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
        verify(this)

        val exporter = Exporter()
        exporter.export(this)

        System.exit(0)
    }
}
