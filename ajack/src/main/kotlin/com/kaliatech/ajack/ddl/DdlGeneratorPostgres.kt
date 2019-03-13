/*
 * Copyright (c) 2019 Joshua Sanderson
 */
package com.kaliatech.ajack.ddl

import com.healthmarketscience.jackcess.*

class DdlGeneratorPostgres(db: Database) : DdlGenerator(db) {

    override fun generateTable(table: Table, prefix: String, caseSensitive: Boolean): String {
        val str = StringBuilder()

        var tableName = prefix + table.name
        tableName = if (caseSensitive) tableName else tableName.toLowerCase()
        var sqlTableName = if (caseSensitive) "\"$tableName\"" else tableName
        str.append("CREATE TABLE $sqlTableName (\n")

        var cols = table.columns

        for (col in cols) {
            val colName = if (caseSensitive) col.name else col.name.toLowerCase()
            var sqlColName = if (caseSensitive) "\"$colName\"" else colName
            //sqlColName = sqlColName.replace("[\\\\s+]", "")

            sqlColName = sqlColName.replace("[^A-Za-z0-9\\-_]".toRegex(), "_")
            str.append("  $sqlColName " + getColDef(col))

            val props: PropertyMap = col.properties
            if (props.get("REQUIRED")?.value == true) {
                str.append(" NOT NULL")
            }

            if (!col.equals(cols.last())) {
                str.append(",\n")
            }
        }

        for (idx in table.indexes) {
            if (idx.isPrimaryKey) {
                // ALTER TABLE "aircraft" ADD CONSTRAINT "aircraft_pkey" PRIMARY KEY ("ev_id", "Aircraft_Key");
                str.append(
                    String.format(
                        ",\n\n  PRIMARY KEY(%s)\n",
                        idx.columns.asSequence().map { it.name.toLowerCase() }.joinToString(",")
                    )
                )
            }
        }

        str.append("\n);\n\n")

        return str.toString()
    }

    private fun getColDef(col: Column): String {
        var colDefSql = mapDatatype(col)
        return when (colDefSql) {
            "VARCHAR" -> "VARCHAR (" + col.length + ")"
            else -> colDefSql
        }
    }

    override fun getForeignKeyDdl(table: Table): String {
        return ""
    }

    private fun mapDatatype(column: Column): String {
        when (column.type) {
            DataType.BOOLEAN -> return "BOOL"

            DataType.BYTE -> return "SMALLINT"

            DataType.INT,
            DataType.LONG,
            DataType.MONEY -> return "INTEGER"

            DataType.BIG_INT -> return "BIGINT"

            DataType.SHORT_DATE_TIME -> return "TIMESTAMP"

            DataType.DOUBLE -> return "DOUBLE PRECISION"

            DataType.FLOAT -> return "REAL"

            DataType.NUMERIC -> return "NUMERIC"

            DataType.TEXT,
            DataType.GUID -> return "VARCHAR"

            DataType.MEMO -> return "TEXT"

            DataType.BINARY,
            DataType.OLE -> return "BLOB"

            else -> throw IllegalArgumentException("Unsupported data type: " + column.type)
        }
    }
}

