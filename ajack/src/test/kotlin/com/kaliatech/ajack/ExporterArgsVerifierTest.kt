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
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.test.Ignore


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
    fun `Export with dest`() {
        val exportDir = tempDir!!.resolve("export")
        val mdbFile = createFakeMdb(tempDir)

        val cmd = ExportCommand()
        cmd.mdbFilePath = mdbFile.toString()
        cmd.destDir = exportDir.toString()
        verify(cmd)

        Assertions.assertTrue(exportDir.toFile().isDirectory, "Dest should be a a directory")
        Assertions.assertTrue(exportDir.toFile().exists(), "Dest does not exist")

    }

    private fun createFakeMdb(tempDir: Path?): File? {
        val mdbDir = tempDir!!.resolve("mdb")
        val mdbFile = mdbDir!!.resolve("fakedb.mdb").toFile()
        Files.createDirectories(mdbDir)
        PrintWriter(mdbFile).use { out -> out.println("test") }
        return mdbFile
    }

    /**
     * This might result with different exception on other platforms. Not really an important test, so ignored.
     */
    @Ignore
    @Test
    fun `Export with bad dest`() {
        kotlin.test.assertFailsWith(InvalidPathException::class, "Should fail with invalid path") {
            val cmd = ExportCommand()
            cmd.mdbFilePath = createFakeMdb(tempDir).toString()
            cmd.destDir = "b@d::test/bad"
            verify(cmd)
        }
    }

    @Test
    fun `Export when dest already exists`() {
        kotlin.test.assertFailsWith(FileAlreadyExistsException::class, "Should fail with existing path") {
            val cmd = ExportCommand()
            cmd.mdbFilePath = createFakeMdb(tempDir).toString()
            cmd.destDir = tempDir.toString()
            verify(cmd)
        }
    }

}

