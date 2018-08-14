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

# Creating projects
```
insert into projects(project_code, project_title, config, user_id, public) VALUES ('PROJECT_CODE', 'PROJECT_TITLE', '{}', 1, true);
select max(id) from projects;
insert into user_projects(project_id, user_id) VALUES (PROJECT_ID, 1);
select create_project_schema(PROJECT_ID, 'biscicoldev')
```
Currently loadinged INDO projects:
3.ACEH  4.AMANDA  5.BALI  6.NOAA  7.PEER  8.PIRE  9.PNMNH  10.SERIBU  11.TIMOR

# Creating configuration File
```
cd ../geome-configurator/bin
# FOLLOW INSTRUCTIONS in the ../geome-configurator/README.md
```

# Loading Biocode data into GeOME steps
```
# Modify Java code for export data
# then when done, run...
ant build

# update biocode data so it conforms to geome rules, see the statements at:
# mysql.updates

#Generate Data
./exportForGeome.sh

# Load Data
python ../geome-db/scripts/biocodeLoader.py --help
python ../geome-db/scripts/biocodeLoader.py 3 W2wdMmeGvz6dPGhRdWg3 output/geome/indo/ACEH
python ../geome-db/scripts/biocodeLoader.py 4 RkG54qJjRCHvawngVNqm output/geome/indo/AMANDA
python ../geome-db/scripts/biocodeLoader.py 5 vtusJCR3yrypU-57Q4ZV output/geome/indo/BALI
python ../geome-db/scripts/biocodeLoader.py 6 vtusJCR3yrypU-57Q4ZV output/geome/indo/NOAA
python ../geome-db/scripts/biocodeLoader.py 7 vtusJCR3yrypU-57Q4ZV output/geome/indo/PEER
python ../geome-db/scripts/biocodeLoader.py 8 vtusJCR3yrypU-57Q4ZV output/geome/indo/PIRE
python ../geome-db/scripts/biocodeLoader.py 9 vtusJCR3yrypU-57Q4ZV output/geome/indo/PNMNH
python ../geome-db/scripts/biocodeLoader.py 10 vtusJCR3yrypU-57Q4ZV output/geome/indo/SERIBU
python ../geome-db/scripts/biocodeLoader.py 11 vtusJCR3yrypU-57Q4ZV output/geome/indo/TIMOR
```
