#!/usr/bin/env bash
# Build & (optionally) run CAER VODRAN.
#   ./build.sh         regen source + compile
#   ./build.sh run     regen + compile + run interactively
set -e
export JAVA_HOME=/home/pwood/tools/jdk-21.0.11+10
export PATH="$JAVA_HOME/bin:/home/pwood/tools/sbt/bin:$PATH"
HERE="$(cd "$(dirname "$0")" && pwd)"
JAR="$HERE/ActionC/target/scala-2.12/ActionC.jar"

python3 "$HERE/build/gen.py"
echo "compiling caer_vodran.actionc ..."
( cd "$HERE" && java -jar "$JAR" caer_vodran.actionc )
echo "compiled OK -> Game.class + caer_vodran.class"

if [ "$1" = "run" ]; then
  ( cd "$HERE" && java caer_vodran )
fi
