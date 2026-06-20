#!/usr/bin/env bash
# Compile + run the final project on macOS / Linux.
# Usage: from inside the final-project folder, run:  ./run.sh
set -e

# Move to the folder this script lives in, so relative paths (db/) always work.
cd "$(dirname "$0")"

mkdir -p bin db

# The ':' is the classpath separator on macOS/Linux.
CP="lib/*"

echo "Compiling..."
javac -cp "$CP" -d bin src/*.java

echo "Running..."
java -cp "bin:$CP" Main
