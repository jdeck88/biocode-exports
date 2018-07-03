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


