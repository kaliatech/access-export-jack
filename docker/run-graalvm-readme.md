From within the bash shell of the docker machine:

Build project, outputting uber jar in to /build/libs:
* `gradle build shadowJar`

The gradle build is setup to generate picoli meta data needed by graalvm.

Compile with graalvm from within the libs directory:
* `native-image -H:+ReportUnsupportedElementsAtRuntime -H:ReflectionConfigurationFiles=../cli-reflect.json --static --no-server -jar ajack-1.0.0-all.jar`


