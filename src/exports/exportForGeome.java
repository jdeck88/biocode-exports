package exports;


import org.apache.commons.cli.*;
import updates.universalUpdater;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Export for Geome
 */
public class exportForGeome extends connector {
    static final int BUFFER = 2048;

    static File occurrenceDataFile;
    String tmpDirName;


    /*public exportAsDarwinCore() throws Exception {
        super("names");
        tmpDirName = this.processDirectory.getAbsoluteFile().toString();
    }*/

    public exportForGeome (String processDirectory) throws Exception {
        super("names");

        if (processDirectory == null) {
            tmpDirName = this.processDirectory.getAbsoluteFile().toString();
        } else {
            tmpDirName = processDirectory;
        }
    }

    /**
     * Dump Names
     *
     * @throws java.sql.SQLException
     */
    public String dumpCollectingEvents(String projectCode) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "select \n" +
                        "  character_sanitizer(e.Coll_EventID_collector) as eventID,\n" +
                        "  e.Collector_List as recordedBy,\n" +
                        "  e.YearCollected as year,\n" +
                        "  e.MonthCollected as month,\n" +
                        "  e.DayCollected as day,\n" +
                        "  e.TimeofDay as eventTime, \n" +
                        "  e.ContinentOcean as continent,\n" +
                        "  e.IslandGroup as islandGroup,\n" +
                        "  e.Island as island,\n" +
                        "  e.Country as country,\n" +
                        "  e.StateProvince as stateProvince,\n" +
                        "  e.County as county,\n" +
                        "  e.Locality as locality,\n" +
                        "  e.DecimalLongitude as decimalLatitude,\n" +
                        "  e.DecimalLatitude as decimalLongitude,  \n" +
                        "  e.HorizontalDatum as geodeticDatum,\n" +
                        "  cast(e.MaxErrorInMeters as unsigned integer) as coordinateUncertaintyInMeters,\n" +
                        "  cast(e.MinElevationMeters as unsigned integer) as minimumElevationInMeters,\n" +
                        "  cast(e.MaxElevationMeters as unsigned integer) as maximumElevationInMeters,\n" +
                        "  cast(e.MinDepthMeters as unsigned integer) as minimumDepthInMeters,\n" +
                        "  cast(e.MaxDepthMeters as unsigned integer) as maximumDepthInMeters,  \n" +
                        "  cast(e.IndividualCount as unsigned integer) as individualCount,\n" +
                        "  e.Habitat as habitat,\n" +
                        "  e.Collection_Method as samplingProtocol,\n" +
                        "  e.Remarks as eventRemarks,\n" +
                        "  e.VerbatimLongitude as verbatimLongitude,\n" +
                        "  e.VerbatimLatitude as verbatimLatitude,\n" +
                        "  e.Coll_EventID_collector as fieldNumber,\n" +
                        "  e.TaxTeam as collection_code,\n" +
                        "  e.guid as guid\n" +
                        "FROM biocode b, biocode_collecting_event e\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND b.projectCode = '" + projectCode + "';\n";


        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Collecting_Events.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }
    public String dumpTissues(String projectCode) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "select \n" +
                       " concat(character_sanitizer(b.specimen_num_collector),'.',t.tissue_num) as tissueID,\n" +
                       " t.HoldingInstitution,\n" +
                       " t.OtherCatalogNum,\n" +
                       " t.DateFirstEntered,\n" +
                       " t.DateLastModified,\n" +
                       " t.year,\n" +
                       " t.month,\n" +
                       " t.day,\n" +
                       " t.person_subsampling,\n" +
                       " t.container,\n" +
                       " t.preservative,\n" +
                       " t.tissuetype,\n" +
                       " t.format_name96,\n" +
                       " t.well_number96,\n" +
                       " t.molecular_id,\n" +
                       " t.notes,\n" +
                       " t.from_tissue,\n" +
                       " t.tissue_barcode,\n" +
                       " t.tissue_remaining,\n" +
                       " t.guid\n" +
                        "FROM biocode b, biocode_collecting_event e, biocode_tissue t\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND b.bnhm_id=t.bnhm_id\n" + 
                        "AND b.projectCode = '" + projectCode + "';\n";


        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Tissues.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }

    public String dumpSpecimens(String projectCode) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "select \n" +
                        "  character_sanitizer(b.Specimen_Num_Collector) as specimenID,\n" +
                        "  b.HoldingInstitution as institutionCode,\n" +
                        "  b.Specimen_Num_Collector as recordNumber,\n" +
                        "  b.lowesttaxon_generated as scientificName,\n" +
                        "  b.Kingdom as kingdom,\n" +
                        "  b.Phylum as phylum,\n" +
                        "  b.Class as class,  \n" +
                        "  b.Ordr as 'order',\n" +
                        "  b.Family as family,\n" +
                        "  b.Genus as genus,\n" +
                        "  b.Subgenus as subgenus,\n" +
                        "  b.SpecificEpithet as specificEpithet,\n" +
                        "  b.SubspecificEpithet as infraspecificEpithet,\n" +
                        "  b.IdentifiedBy as identifiedBy,\n" +
                        "  b.BasisOfID as identificationRemarks,\n" +
                        "  concat(b.YearIdentified,\"-\",b.MonthIdentified, \"-\",b.DayIdentified) as dateIdentified,\n" +
                        "  b.TypeStatus as typeStatus,\n" +
                        "  b.SexCaste as sex,\n" +
                        "  b.LifeStage as lifeStage,\n" +
                        "  b.bnhm_id as catalogNumber,\n" +
                        "  b.guid as guid\n" +
                        "FROM biocode b, biocode_collecting_event e\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND b.projectCode = '" + projectCode + "';\n";
        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Specimens.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }

    public static void main(String[] args) throws Exception {
        // Some classes to help us
        CommandLineParser clp = new GnuParser();
        HelpFormatter helpf = new HelpFormatter();
        CommandLine cl;

        // The input file
        String projectCode = "";
        String outputDirectory = null;

        // Define our commandline options
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message and exit");
        options.addOption("o", "outputDirectory", true, "Output Directory");
        options.addOption("p", "projectCode", true, "Project Code, e.g. MBIO");

        // Create the commands parser and parse the command line arguments.
        try {
            cl = clp.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // No options returns help message
        if (cl.getOptions().length < 2) {
            helpf.printHelp("exportForGeome ", options, true);
            return;
        }

        if (cl.hasOption("h")) {
            helpf.printHelp("exportForGeome ", options, true);
            return;
        }
        if (cl.hasOption("o")) {
            outputDirectory = cl.getOptionValue("o");
        }
        if (cl.hasOption("p")) {
            projectCode = cl.getOptionValue("p");
        }

        // Dump Data
        exportForGeome d = new exportForGeome(outputDirectory);
        d.dumpSpecimens(projectCode);
        d.dumpCollectingEvents(projectCode);
        d.dumpTissues(projectCode);

        // Dump EML
        /*String emlFileString = outputDirectory + File.separatorChar + "eml.xml";
        PrintWriter eml = new PrintWriter(emlFileString);
        eml.println(getEml());
        eml.close();
        */

    }

    public static File zip(List<File> files, String filename) {
        File zipfile = new File(filename);
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        try {
            // create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            // compress the files
            for (int i = 0; i < files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).getCanonicalPath());
                // add ZIP entry to output stream
                out.putNextEntry(new ZipEntry(files.get(i).getName()));
                // transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // complete the entry
                out.closeEntry();
                in.close();
            }
            // complete the ZIP file
            out.close();
            return zipfile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

}
