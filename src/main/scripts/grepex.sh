#!/bin/bash
DIR="$(dirname "$(readlink -f "$0")")"
java -jar $DIR/grepex.jar $1
