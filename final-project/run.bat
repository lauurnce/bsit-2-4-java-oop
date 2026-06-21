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
REM Gather all .java files under src\ (recursively) into a sources list.
if exist sources.txt del sources.txt
for /r src %%f in (*.java) do echo %%f>> sources.txt
javac -cp "%CP%" -d bin @sources.txt
del sources.txt
if errorlevel 1 goto :error

echo Running...
java -cp "bin;%CP%" app.Main
goto :eof

:error
echo.
echo Build failed. Make sure the JDK is installed and 'javac' is on your PATH.
pause
