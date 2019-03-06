# Overview
Export Microsoft Access MDB tables schema DDL and data for importing to PostgreSQL. No ODBC or instance of MS Access is required.  Native executables for Windows, Linux, and OSX.

## Download
 * Windows: [ajack-1.0.0-win-x64.zip]() - Tested on windows 10.
 * Linux: [ajack-1.0.0-linux-x64.tar.gz]() - Tested on Ubuntu-18.04.
 * OSX: [ajack-1.0.0-darwin-x64.tar.gz]()

## Usage
Example: `ajack example.mdb`
 * Creates a folder for holding all exported files. Defaults to "./export-yyyyMMddHHmmss".
 * Writes schema.sql, containing DDL for creating tables
 * Writes {table}.csv for each table, containing CSV formatting and escapes compatible with postgresql COPY (or pgsql /copy) command
 * Writes import-schema&#46;sh, containing helper bash script for executing psql with the schema file
 * Writes import-data&#46;sh, containing helper bash script for executing psql for all tables

Options:
 * **-t,--tables <tables>** : Comma delimited list of specific tables to output
 * **-o,--output <outputdir>** : Destination directory. Will be created if doesn't exist.
 * **-s,--schema <true/false>** : Output schema ddl file. Defaults to true.
 * **-f,--format <format>**: Output format. Currently only "postgres-csv" is supported.

## Alternatives
Instead of writing yet another access export tool, I could have also forked/tweaked/scripted a solution using existing tools, most of which are more flexible and robust than the tool I created. I created this tool primarily as a way to experiment with a few technologies. Many will probably be better served by one of the more vetted tools below:

 * [hzpz/access-export](https://github.com/hzpz/access-export)
 Also uses Jackexcess. Java based. Exports to CSV and SQLite.  Well written. Currently uses older Jackexcess library, but probably easy to update. I almost forked this project instead of starting greenfield.  However, I wanted to experiment with Kotlin, GraalVM, and Picoli.
 
 * [mdbtools](https://github.com/brianb/mdbtools)
 Probably the most well known native MDB export tool and one of the oldest.  This would likely work well for many projects. Its support for PostgreSQL compatible exports seems problematic though. For my DB, the exported schema was able to be imported, but with various errors.  I was not able to import exported data due to handling of boolean values and various escaping issues, at least not with out additional sed/awk/regex work.  
   * See [this thread](https://www.postgresql.org/message-id/flat/87ej9xqha4.fsf%40patagonia.sebmags.homelinux.org) started by Seb on postgres mailing list about dealing with the int to boolean cast.
   * See [this message and patch](https://sourceforge.net/p/mdbtools/mailman/message/5998326/) from Patrick Welche about changes needed to support postgresql from mdb-export directly.
   * Example [python snippet](https://gist.github.com/mywarr/9908044) using mdb-tools
   * Be sure to review the [issues](https://github.com/brianb/mdbtools/issues) list.  This, ultimately, is why I decided not to continue with mdbtools. It would take additional work to verify there was no data loss after import.
   
 * [UCanAccess JDBC Driver](http://ucanaccess.sourceforge.net)
 This looked very promising and would likely work well for many projects.  It relies on Jackexcess and internally works by creating a mirror HSQL database to handle various JDBC requirements.  However, with the particular (very large) Access database I was working with, it would never finish the connection and seemingly went in to an infinite loop. This happened no matter how much memory I gave it, if I used file based HSQL storage, and with various other configuration settings.  I was able to cancel the process, and when using file backed HSQL mirror, I could then subsequently connect to read the tables at had exported, but it included data only up until the point it hung.
 
 * Programmatic with [Jackcess](https://jackcess.sourceforge.io/)
 In many ways "ajack" is nothing more than a wrapper around Jackexcess, adding some specific handling for postgres.  The progammatically inclined could easily make use of Jackexcess directly. Of special interest would be Jackexcess's [ExportUtil](https://jackcess.sourceforge.io/apidocs/index.html?com/healthmarketscience/jackcess/util/ExportUtil.html) class.

 * Exporting directly out of [Access](https://products.office.com/en-us/access)
 There are a number of free VBA type modules that can be added to an Access database to export directly from Access in to various formats.  I previously used this approach.  However, it requires Windows and MS Access. A few of these are linked on the postgres wiki page below.
 
 * Exporting via ODBC
 Many tools, often commercial, are able to export from Access via an ODBC connection.  ODBC generally only works well on Windows though. Requires Access and additional setup.

 The postgresql wiki page: [Converting_from_other_Databases_to_PostgreSQL](https://wiki.postgresql.org/wiki/Converting_from_other_Databases_to_PostgreSQL#Microsoft_Access), lists various options, including some of the above.
 
 ## License and Dependencies
 This ajack tool is published under MIT License. It uses code from these libraries at runtime:

  * 
 
   


