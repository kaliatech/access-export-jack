/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class ExporterArgsVerifierTest {

    var tempDir: Path? = null


    @BeforeEach
    fun beforeEach() {
        tempDir = Files.createTempDirectory("test-")
    }

    @AfterEach
    fun afterEach() {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach { it.delete() }

        Assertions.assertFalse(tempDir!!.toFile().exists(), "Temp dir should not not exist after cleanup")
    }

    @Test
    fun `Export with valid dest`() {
        val exportDir = tempDir!!.resolve("export")
        val mdbFile = createFakeMdb(tempDir)

        val cmd = ExportCommand()
        cmd.mdbFilePath = mdbFile.toString()
        cmd.destDir = exportDir.toString()
        verify(cmd)

        Assertions.assertTrue(exportDir.toFile().isDirectory, "Dest should be a a directory")
        Assertions.assertTrue(exportDir.toFile().exists(), "Dest does not exist")

    }

    @Test
    fun `Export with bad dest`() {
        val cmd = ExportCommand()
        cmd.mdbFilePath = createFakeMdb(tempDir).toString()
        cmd.destDir = "b@d::test/bad"
        Assertions.assertFalse(verify(cmd))
    }

    @Test
    fun `Export when dest already exists and is not empty`() {
        //kotlin.test.assertFailsWith(FileAlreadyExistsException::class, "Should fail with existing path") {
        val cmd = ExportCommand()
        cmd.mdbFilePath = createFakeMdb(tempDir).toString()
        cmd.destDir = tempDir?.resolve("export").toString()

        // Write temp file to make sure is not empty
        Files.createDirectories(Paths.get(cmd.destDir))
        val randFile = Paths.get(cmd.destDir, "tmp.txt").toFile()
        PrintWriter(randFile).use { out -> out.println("test") }

        Assertions.assertTrue(verify(cmd))
    }

    private fun createFakeMdb(tempDir: Path?): File? {
        val mdbDir = tempDir!!.resolve("mdb")
        val mdbFile = mdbDir!!.resolve("fakedb.mdb").toFile()
        Files.createDirectories(mdbDir)
        PrintWriter(mdbFile).use { out -> out.println("test") }
        return mdbFile
    }

}

