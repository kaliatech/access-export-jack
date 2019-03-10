/*
 * Copyright (c) 2019 Joshua Sanderson
 */

package com.kaliatech.ajack

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun verify(cmd: ExportCommand): Boolean {
    val topLevelClass = object : Any() {}.javaClass.enclosingClass
    val log: Logger = LoggerFactory.getLogger(topLevelClass)

    if (StringUtils.isEmpty(cmd.mdbFilePath)) {
        log.error("MDB file path is required.")
        return false
    }

    val mdbFile = File(cmd.mdbFilePath)
    if (!mdbFile.exists()) {
        log.error("MDB file not found.")
        return false
    }

    try {
        val destDirPath = Paths.get(cmd.destDir)

        // If we wanted to prevent exporting to non-empty directory:
//        if (!cmd.overwrite && destDirPath.toFile().exists()) {
//            Files.newDirectoryStream(destDirPath).use {
//                if (it.any()) {
//                    log.error("Destination directory already exists and is not empty.")
//                    return false
//                }
//            }
//        }

        Files.createDirectories(destDirPath)
    } catch (e: Exception) {
        log.error("Unable to create destination directory.")
        return false
    }

    return true
}
