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
import java.util.HashMap;
import java.util.Map;

/**
 * Export for INDO project into Geome
 *
 * This class exports the INDO project into Geome.
 * Chris would like to use the specimen_num_collector codes to organize
 * this dump into discreet expeditions... This works using the mapping
 * we find for specimen_num_collector codes to expedition labels.
 * The only caveat is that there are 22 collecting_events which are not able
 * to be exported that have no link to specimens.  Since the field we are 
 * dividing into expeditions lives in the specimen table in biocode, there is no  way
 * to map these collecting events into expeditions.  I think we should just recreate 
 * these from the biocode database, which can be found using the following query:
 *
 * select * from biocode_collecting_event where eventid not in 
 * (select coll_eventid from biocode where projectCode = 'INDO') and projectCode = 'INDO';
 */
public class exportIndoForGeome extends connector {
    static final int BUFFER = 2048;

    static File occurrenceDataFile;
    String tmpDirName;


    /*public exportAsDarwinCore() throws Exception {
        super("names");
        tmpDirName = this.processDirectory.getAbsoluteFile().toString();
    }*/

    public exportIndoForGeome (String processDirectory) throws Exception {
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
    public String dumpCollectingEvents(String projectCode, String prefixList, String expedition) throws SQLException, IOException {
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
                        "FROM  biocode b, biocode_collecting_event e\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND e.projectCode = '" + projectCode + "'\n";
	sql += queryPrefixList(prefixList);
	sql += " group by eventID";
        occurrenceDataFile = new File(tmpDirName + File.separatorChar + expedition + "_Collecting_Events.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }
    
    /*
    A function that gets called a few times here to assemble the prefixList query
    this is a very INDO-specific function 
    */
    public String queryPrefixList(String prefixList) {
	String sql = "";	
	sql += "AND (";

	String[] prefixListArray = prefixList.split(",");
	for (String prefix : prefixListArray) {
	   sql += "b.specimen_num_collector like '" + prefix+ "%' OR ";
	}
	// Remove the last OR from the string (including two spaces)
	sql = sql.substring(0,sql.length() - 4);
	sql += ")";
	return sql;
    }

    public String dumpTissues(String projectCode, String prefixList, String expedition) throws SQLException, IOException {
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
                        "AND b.projectCode = '" + projectCode + "'\n";
	sql += queryPrefixList(prefixList);

        occurrenceDataFile = new File(tmpDirName + File.separatorChar + expedition + "_Tissues.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }

    public String dumpSpecimens(String projectCode, String prefixList, String expedition) throws SQLException, IOException {
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
                        "FROM biocode b, biocode_collecting_event e \n" +
                        "where b.Coll_EventID = e.EventID \n" +
                        "AND b.projectCode = '" + projectCode + "'\n";
	sql += queryPrefixList(prefixList);

        occurrenceDataFile = new File(tmpDirName + File.separatorChar + expedition + "_Specimens.txt");
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
        exportIndoForGeome d = new exportIndoForGeome(outputDirectory);


	// For INDO dump, we want to map prefixes to funding sources... we use
	// here a delimited string as the key value in a map and point to a single
	// expedition value
	Map<String, String> map = new HashMap<String,String>();
	map.put("ACEH\\_","ACEH");
	map.put("AUS,FLA,FL_PF,INV-CRU,NMV,QMW,SBD,ULLZ,USNM,ZRC","AMANDA");
	map.put("BALI,CPM,IBRC,LISM,SERIBU,SRB,WPSM","BALI");
	map.put("SS","NOAA");
	map.put("ACEHPEER\\_,PEER,PER,PPER","PEER");
	map.put("PIRE","PIRE");
	map.put("PNMNH","PNMNH");
	map.put("TIM","TIMOR");



    	for (Map.Entry<String, String> entry : map.entrySet()) {
	    String prefixList = entry.getKey().toString();
	    String expedition = entry.getValue().toString();
	    d.dumpSpecimens(projectCode, prefixList, expedition);
	    d.dumpCollectingEvents(projectCode,prefixList, expedition);
	    d.dumpTissues(projectCode, prefixList, expedition);
        }

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
