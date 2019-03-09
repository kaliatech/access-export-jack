/*
 * Copyright (c) 2019 Joshua Sanderson
 */

package com.kaliatech.ajack

import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

fun verify(cmd: ExportCommand) {
    if (StringUtils.isEmpty(cmd.mdbFilePath)) {
        throw FileNotFoundException("MDB file not found.")
    }

    val mdbFile = File(cmd.mdbFilePath)
    if (!mdbFile.exists()) {
        throw FileNotFoundException("MDB file not found.")
    }

    val destDirPath = Paths.get(cmd.destDir)
    if (destDirPath.toFile().exists()) {
        throw FileAlreadyExistsException(destDirPath.toFile(), null, "Destination directory exists.")
    }
    Files.createDirectories(destDirPath)
}
