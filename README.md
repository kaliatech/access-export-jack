# Overview
Export data from Microsoft Access Database (mdb) files. MS Access is not required. Uses the [Jackcess](https://jackcess.sourceforge.io) library. Cross platform.

## Download 
 * work-in-progress
<!---
 * Linux-Native: [ajack-1.0.0-linux-x64.tar.gz](https://github.com/kaliatech/access-export-jack/releases) - Tested on Ubuntu-18.04.
 * OSX-Native: TBD
 * Java: [ajack-1.0.0.jar](https://github.com/kaliatech/access-export-jack/releases) - Anywhere with Java 8 or newer.
--->
 
The native linux version also works in the Windows Subsystem for Linux (WSL).

## Usage
```console
# Linux or WSL
ajack example.mdb

# Java
java -jar ajack-1.0.0.jar example.mdb
```

### Results
Creates a folder for holding all exported files. Defaults to "./export-yyyyMMddHHmmss". Files:
 * **schema.sql** - DDL for creating tables and indexes.
 * **{table}.csv** - Data using CSV formatting and escapes compatible with *postgresql* [COPY](https://www.postgresql.org/docs/current/sql-copy.html) or *pgsql* [/copy](https://www.postgresql.org/docs/current/app-psql.html#APP-PSQL-META-COMMANDS-COPY) commands.
 * **import-schema&#46;sh** - Utility bash script for executing psql for generating schema.
 * **import-data&#46;sh** - Utility bash script for executing psql for loading all tables.

### Options
```console
  Usage: ajack [-hov] [-cs] [-d=<dir>] [-f=<format>] [-p=<prefix>]
               [-cm=<String=String>]... [-t=table[,table...]]... <mdb-file>
			   
        <mdb-file>            mdb file
        -cm, --column=<String=String>
                              Rename specific columns.
        -cs, --case-sensitive Case sensitive DDL. Default false.
    -d, --destination=<dir>   Output directory. Default: ./export-yyyyMMdd-HHmmss
    -f, --format=<format>     Output format. Valid values: POSTGRES_CSV
    -h, --help                print this help and exit
    -o, --overwrite           Overwrite any existing files.
    -p, --prefix=<prefix>     Table name prefix.
    -t, --table=table[,table...]
                              Specific tables to export. Default: all
    -v, --version             print version and exit
	
  Copyright(c) 2019
```

## Alternatives
Instead of writing yet another access export tool, I could have also forked/tweaked/scripted a solution using existing tools, most of which are more flexible and robust than the tool I created. I created this tool primarily as a way to experiment with a few technologies. Many will probably be better served by one of the more vetted tools below:

 * **[hzpz/access-export](https://github.com/hzpz/access-export)**
 Java based and also uses [Jackcess](https://jackcess.sourceforge.io/).  Exports to CSV and SQLite.  Well written. I almost forked this instead of starting a new project from scratch. (I did submit [PR](https://github.com/hzpz/access-export/pull/5) to update to latest Jackcess.)  However, I wanted to experiment with Kotlin, GraalVM, and Picoli.
 
 * **[mdbtools](https://github.com/brianb/mdbtools)**
 Probably the most well known native MDB export tool and one of the oldest.  This would likely work well for many projects. Its support for PostgreSQL compatible exports seems problematic though. For my Access DB, the exported schema was able to be imported to postgres, but with various errors.  I was not able to import exported data due to handling of boolean values and various escaping issues, at least not with out additional sed/awk/regex work.  
   * See [this thread](https://www.postgresql.org/message-id/flat/87ej9xqha4.fsf%40patagonia.sebmags.homelinux.org) started by Seb on postgres mailing list about dealing with the int to boolean cast.
   * See [this message and patch](https://sourceforge.net/p/mdbtools/mailman/message/5998326/) from Patrick Welche about changes needed to support postgresql from mdb-export directly.
   * Example [python snippet](https://gist.github.com/mywarr/9908044) using mdb-tools
   * Be sure to review the [issues](https://github.com/brianb/mdbtools/issues) list.  This, ultimately, is why I decided not to continue with mdbtools. It would take additional work to verify there was no data loss after import.
   
 * **[UCanAccess JDBC Driver](http://ucanaccess.sourceforge.net)**
 This looked very promising and would likely work well for many projects.  It relies on Jackcess and internally works by creating a mirror HSQL database to handle various JDBC requirements.  However, with the particular (very large) Access database I am working with, it would never finish the connection and seemingly went in to an infinite loop. This happened no matter how much memory I gave it, if I used file based HSQL storage, and with various other configuration settings.  I was able to cancel the process, and when using file backed HSQL mirror, I could then subsequently connect to read the tables it had exported, but it included data only up until the point it hung.
 
 * **Programmatic with [Jackcess](https://jackcess.sourceforge.io/)**
 In many ways "ajack" is nothing more than a wrapper around Jackcess, adding some specific handling for postgres.  The progammatically inclined could easily make use of Jackexcess directly. Of special interest would be Jackcess's [ExportUtil](https://jackcess.sourceforge.io/apidocs/index.html?com/healthmarketscience/jackcess/util/ExportUtil.html) class.

 * **Exporting directly out of [Access](https://products.office.com/en-us/access)**
 There are a number of free VBA type modules that can be added to an Access database to export directly from Access in to various formats.  I previously used this approach.  However, it requires Windows and MS Access. A few of these tools are linked on the postgres wiki page below, and many more available from google search.
 
 * **Exporting via ODBC**
 Many tools, often commercial and with an ETL slant, are able to export from Access via an ODBC connection.  ODBC generally only works well on Windows though. Requires Access and additional setup.

 The postgresql wiki page: [Converting_from_other_Databases_to_PostgreSQL](https://wiki.postgresql.org/wiki/Converting_from_other_Databases_to_PostgreSQL#Microsoft_Access), lists various options, including some of the above.
 
 ## License and Dependencies
 This code is published under MIT License. It uses a number of libraries that have compatible open source libraries.  
