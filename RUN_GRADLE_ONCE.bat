@echo off
rem Helper to run the single allowed gradle invocation and write to gradle-once-compile.log
gradlew.bat clean compileJava > gradle-once-compile.log 2>&1
echo Gradle run finished, output written to gradle-once-compile.log