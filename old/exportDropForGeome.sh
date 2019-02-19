#!/bin/bash
/usr/bin/java -cp $HOME/code/biocode-exports/lib/*:$HOME/code/biocode-exports/:$HOME/code/biocode-exports/out/production/biocode-exports exports.exportDropForGeome -o $HOME/code/biocode-exports/output/geome/drop -p DROP

# copy this code, make sure you modify load script below to pont to develop or geome
#python ../geome-db/scripts/biocodeLoader.py 61 wKb8FJkXugyXqcZW2KkD output/geome/drop/DROP --accept_warnings True

