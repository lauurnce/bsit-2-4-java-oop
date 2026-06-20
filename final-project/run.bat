@echo off
REM Compile + run the final project on Windows.
REM Usage: double-click this file, or from a terminal inside final-project run: run.bat

REM Move to the folder this script lives in, so relative paths (db\) always work.
cd /d "%~dp0"

if not exist bin mkdir bin
if not exist db mkdir db

REM The ';' is the classpath separator on Windows.
set CP=lib/*

echo Compiling...
javac -cp "%CP%" -d bin src/*.java
if errorlevel 1 goto :error

echo Running...
java -cp "bin;%CP%" Main
goto :eof

:error
echo.
echo Build failed. Make sure the JDK is installed and 'javac' is on your PATH.
pause
