/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack.ddl

import com.healthmarketscience.jackcess.Database
import com.healthmarketscience.jackcess.Table
import com.kaliatech.ajack.ExportCommand

abstract class DdlGenerator(db: Database) {
    val db: Database = db

    fun getSchema(cmd: ExportCommand): String {
        val tablesDdl = StringBuilder()
        val fksDdl = StringBuilder()
        for (tableName in cmd.tables) {
            val t = db.getTable(tableName);
            tablesDdl.append(generateTable(t, cmd.prefix, cmd.caseSensitive, cmd.columnRenames))
            fksDdl.append(getForeignKeyDdl(t))
        }
        return tablesDdl.append(fksDdl).toString()
    }

    abstract fun generateTable(
        table: Table,
        prefix: String,
        caseSensitive: Boolean,
        columnRenames: Map<String, String>
    ): String

    abstract fun getForeignKeyDdl(table: Table): String
}

