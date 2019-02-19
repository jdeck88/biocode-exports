#!/bin/bash
/usr/bin/java -cp $HOME/code/biocode-exports/lib/*:$HOME/code/biocode-exports/:$HOME/code/biocode-exports/out/production/biocode-exports exports.exportAllBiocodeForGeome -o $HOME/code/biocode-exports/output/geome/allbiocode -p "$1" -e "$2"
