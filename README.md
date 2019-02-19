This set of code is purely meant for exporting Moorea Biocode Project data to different formats.
The goal is to export data in the following formats

ISATab
BOLD
Merritt
DwC-archive
Geome

Since we expect the process of data publishing for the Moorea Biocode Project to be involved and ongoing,
this code-base will act as the source for all export data.   

To run this codebase, do the following:

1. fork this repository

2. rename biocode-exports.template.props to biocode-exports.props (and change property values accordingly).
biocode-exports.props should NOT be committed to github!  It is referenced in .gitignore

3. Run `ant build`.  NOTE: you may need to edit build.properties for your local environment

4. Run the main methods in various classes in the exports package with the appropriate classpaths.  
Each java export class has its own command options and associated help.
The following example exports Biocode Project data to GeOMe:

```
#java -cp {classpaths} exports.exportForGeome -o {outputDirectory} -p {projectCode}
/usr/bin/java -cp /home/jdeck/code/biocode-exports/lib/*:/home/jdeck/code/biocode-exports/:/home/jdeck/code/biocode-exports/out/production/biocode-exports exports.exportForGeome -o /data/ipt/data/resources/biocode/geome/indo -p INDO
```

# Creating projects and configurations
Configuration files are managed in the geome-configurator repository.
See instructions in https://github.com/biocodellc/geome-configurations

# Loading Biocode data into GeOME steps
```
# Modify Java code for export data
# then when done, run...
ant build

# update biocode data so it conforms to geome rules, see the statements at:
# mysql.updates

#Generate Data
./exportForGeome.sh

# Load Biocode Data Example
python ../geome-db/scripts/biocodeLoader.py --help
python ../geome-db/scripts/biocodeLoader.py 3 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/ACEH --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 4 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/AMANDA --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 5 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/BALI --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 6 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/NOAA --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 7 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/PEER --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 8 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/PIRE --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 9 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/PNMNH --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 10 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/SERIBU --accept_warnings True
python ../geome-db/scripts/biocodeLoader.py 11 HNM9Gxg3Un4U7uD-tgnb output/geome/indo/TIMOR --accept_warnings True
``` 
