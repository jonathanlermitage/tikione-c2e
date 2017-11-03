#!/bin/bash

# ---------------------------------------------------------------------------------------------------------------------------
# TikiOne C2E start-up script.
# Used to launch TikiOne C2E with the operating system's (JRE8) JVM and optional ImageMagick.
# ---------------------------------------------------------------------------------------------------------------------------

java -jar -Xms32m -Xmx512m -Dfile.encoding=UTF-8 "c2e.jar" "$@"
