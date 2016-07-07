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
 * Export Biocode Project to Darwin Core Archive
 */
public class exportAsDarwinCore extends connector {
    static final int BUFFER = 2048;

    static File occurrenceDataFile;
    String tmpDirName;


    /*public exportAsDarwinCore() throws Exception {
        super("names");
        tmpDirName = this.processDirectory.getAbsoluteFile().toString();
    }*/

    public exportAsDarwinCore(String processDirectory) throws Exception {
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
    public String dumpProjectCode(String projectCode) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "select \n" +
                        "  b.guid as occurrenceID,\n" +
                        "  'PreservedSpecimen' as basisOfRecord,\n" +
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
                        "  e.guid as eventID,\n" +
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
                        "  b.bnhm_id as catalogNumber,\n" +
                        "  e.TaxTeam as collection_code\n" +
                        "FROM biocode b, biocode_collecting_event e\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND b.projectCode = '" + projectCode + "';\n";


        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "occurrence.txt");
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
            helpf.printHelp("exportAsDarwinCore ", options, true);
            return;
        }

        if (cl.hasOption("h")) {
            helpf.printHelp("exportAsDarwinCore ", options, true);
            return;
        }
        if (cl.hasOption("o")) {
            outputDirectory = cl.getOptionValue("o");
        }
        if (cl.hasOption("p")) {
            projectCode = cl.getOptionValue("p");
        }

        // Update GUIDS before dumping
        universalUpdater updater = new universalUpdater(null);
        updater.updateBiocodeGuid();
        updater.updateBiocodeCollectingEventGuid();
        updater.updateBiocodeTissueGuid();
        updater.updateBiocodeTaxonGuid();

        // Dump Data
        exportAsDarwinCore d = new exportAsDarwinCore(outputDirectory);
        d.dumpProjectCode(projectCode);

        // Dump EML
        /*String emlFileString = outputDirectory + File.separatorChar + "eml.xml";
        PrintWriter eml = new PrintWriter(emlFileString);
        eml.println(getEml());
        eml.close();

        // Dump Meta
        String metaFileString = outputDirectory + File.separatorChar + "meta.xml";
        PrintWriter meta = new PrintWriter(metaFileString);
        meta.println(getMetaXML());
        meta.close();
        */

        // Zip contents
        /*ArrayList FileList = new ArrayList();
        FileList.add(occurrenceDataFile);
        FileList.add(new File(emlFileString));
        FileList.add(new File(metaFileString));
        */

        // Generate ZipFile
        //File zipFile = zip(FileList, outputDirectory + File.separatorChar + "dwca.zip");

        // Clean up
        /*Iterator it = FileList.iterator();
        while (it.hasNext()) {
            File f = (File) it.next();
            try {
                f.delete();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        System.out.println("successfully generated zipFile = " + zipFile.getAbsolutePath());
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

    private static String getMetaXML() {
        return "<archive xmlns=\"http://rs.tdwg.org/dwc/text/\" metadata=\"eml.xml\">\n" +
                "  <core encoding=\"utf-8\" fieldsTerminatedBy=\"\\t\" linesTerminatedBy=\"\\n\" fieldsEnclosedBy=\"\" ignoreHeaderLines=\"1\" rowType=\"http://rs.tdwg.org/dwc/terms/Occurrence\">\n" +
                "    <files>\n" +
                "      <location>occurrence.txt</location>\n" +
                "    </files>\n" +
                "    <id index=\"0\" />\n" +
                "    <field index=\"1\" term=\"http://rs.tdwg.org/dwc/terms/occurrenceID\"/>\n" +
                "    <field index=\"2\" term=\"http://rs.tdwg.org/dwc/terms/basisOfRecord\"/>\n" +
                "    <field index=\"3\" term=\"http://rs.tdwg.org/dwc/terms/institutionCode\"/>\n" +
                "    <field index=\"4\" term=\"http://rs.tdwg.org/dwc/terms/recordNumber\"/>\n" +
                "    <field index=\"5\" term=\"http://rs.tdwg.org/dwc/terms/scientificName\"/>\n" +
                "    <field index=\"6\" term=\"http://rs.tdwg.org/dwc/terms/kingdom\"/>\n" +
                "    <field index=\"7\" term=\"http://rs.tdwg.org/dwc/terms/phylum\"/>\n" +
                "    <field index=\"8\" term=\"http://rs.tdwg.org/dwc/terms/class\"/>\n" +
                "    <field index=\"9\" term=\"http://rs.tdwg.org/dwc/terms/order\"/>\n" +
                "    <field index=\"10\" term=\"http://rs.tdwg.org/dwc/terms/family\"/>\n" +
                "    <field index=\"11\" term=\"http://rs.tdwg.org/dwc/terms/genus\"/>\n" +
                "    <field index=\"12\" term=\"http://rs.tdwg.org/dwc/terms/subgenus\"/>\n" +
                "    <field index=\"13\" term=\"http://rs.tdwg.org/dwc/terms/specificEpithet\"/>\n" +
                "    <field index=\"14\" term=\"http://rs.tdwg.org/dwc/terms/infraspecificEpithet\"/>\n" +
                "    <field index=\"15\" term=\"http://rs.tdwg.org/dwc/terms/identificationRemarks\"/>\n" +
                "    <field index=\"16\" term=\"http://rs.tdwg.org/dwc/terms/dateIdentified\"/>\n" +
                "    <field index=\"17\" term=\"http://rs.tdwg.org/dwc/terms/typeStatus\"/>\n" +
                "    <field index=\"18\" term=\"http://rs.tdwg.org/dwc/terms/sex\"/>\n" +
                "    <field index=\"19\" term=\"http://rs.tdwg.org/dwc/terms/lifeStage\"/>\n" +
                "    <field index=\"20\" term=\"http://rs.tdwg.org/dwc/terms/eventID\"/>\n" +
                "    <field index=\"21\" term=\"http://rs.tdwg.org/dwc/terms/recordedBy\"/>\n" +
                "    <field index=\"22\" term=\"http://rs.tdwg.org/dwc/terms/year\"/>\n" +
                "    <field index=\"23\" term=\"http://rs.tdwg.org/dwc/terms/month\"/>\n" +
                "    <field index=\"24\" term=\"http://rs.tdwg.org/dwc/terms/day\"/>\n" +
                "    <field index=\"25\" term=\"http://rs.tdwg.org/dwc/terms/eventTime\"/>\n" +
                "    <field index=\"26\" term=\"http://rs.tdwg.org/dwc/terms/continent\"/>\n" +
                "    <field index=\"27\" term=\"http://rs.tdwg.org/dwc/terms/islandGroup\"/>\n" +
                "    <field index=\"28\" term=\"http://rs.tdwg.org/dwc/terms/island\"/>\n" +
                "    <field index=\"29\" term=\"http://rs.tdwg.org/dwc/terms/country\"/>\n" +
                "    <field index=\"30\" term=\"http://rs.tdwg.org/dwc/terms/stateProvince\"/>\n" +
                "    <field index=\"31\" term=\"http://rs.tdwg.org/dwc/terms/county\"/>\n" +
                "    <field index=\"32\" term=\"http://rs.tdwg.org/dwc/terms/locality\"/>\n" +
                "    <field index=\"33\" term=\"http://rs.tdwg.org/dwc/terms/decimalLatitude\"/>\n" +
                "    <field index=\"34\" term=\"http://rs.tdwg.org/dwc/terms/decimalLongitude\"/>\n" +
                "    <field index=\"35\" term=\"http://rs.tdwg.org/dwc/terms/geodeticDatum\"/>\n" +
                "    <field index=\"36\" term=\"http://rs.tdwg.org/dwc/terms/coordinateUncertaintyInMeters\"/>\n" +
                "    <field index=\"37\" term=\"http://rs.tdwg.org/dwc/terms/minimumElevationInMeters\"/>\n" +
                "    <field index=\"38\" term=\"http://rs.tdwg.org/dwc/terms/maximumElevationInMeters\"/>\n" +
                "    <field index=\"39\" term=\"http://rs.tdwg.org/dwc/terms/minimumDepthInMeters\"/>\n" +
                "    <field index=\"40\" term=\"http://rs.tdwg.org/dwc/terms/maximumDepthInMeters\"/>\n" +
                "    <field index=\"41\" term=\"http://rs.tdwg.org/dwc/terms/individualCount\"/>\n" +
                "    <field index=\"42\" term=\"http://rs.tdwg.org/dwc/terms/habitat\"/>\n" +
                "    <field index=\"43\" term=\"http://rs.tdwg.org/dwc/terms/samplingProtocol\"/>\n" +
                "    <field index=\"44\" term=\"http://rs.tdwg.org/dwc/terms/eventRemarks\"/>\n" +
                "    <field index=\"45\" term=\"http://rs.tdwg.org/dwc/terms/verbatimLongitude\"/>\n" +
                "    <field index=\"46\" term=\"http://rs.tdwg.org/dwc/terms/verbatimLatitude\"/>\n" +
                "    <field index=\"47\" term=\"http://rs.tdwg.org/dwc/terms/fieldNumber\"/>\n" +
                "  </core>\n" +
                "</archive>";
    }

    public static String getEml() {
        return "<eml:eml xmlns:eml=\"eml://ecoinformatics.org/eml-2.1.1\"\n" +
                "         xmlns:dc=\"http://purl.org/dc/terms/\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"eml://ecoinformatics.org/eml-2.1.1 http://rs.gbif.org/schema/eml-gbif-profile/1.0.2/eml.xsd\"\n" +
                "         packageId=\"5d283bb6-64dd-4626-8b3b-a4e8db5415c3/v68\" system=\"http://gbif.org\" scope=\"system\"\n" +
                "         xml:lang=\"eng\">\n" +
                "\n" +
                "<dataset>\n" +
                "  <alternateIdentifier>33204c15-8004-43d4-81f0-92ce25864554</alternateIdentifier>\n" +
                "  <alternateIdentifier>http://bnhmipt.berkeley.edu/ipt/resource.do?r=biocode</alternateIdentifier>\n" +
                "  <title xml:lang=\"eng\">Moorea Biocode Project</title>\n" +
                "<creator>\n" +
                "    <individualName>\n" +
                "        <givenName>John</givenName>\n" +
                "      <surName>Deck</surName>\n" +
                "    </individualName>\n" +
                "    <organizationName>UC Berkeley</organizationName>\n" +
                "    <positionName>Programmer</positionName>\n" +
                "    <address>\n" +
                "        <deliveryPoint>1096 Valley Life Sciences Building</deliveryPoint>\n" +
                "        <city>Berkeley</city>\n" +
                "        <administrativeArea>California</administrativeArea>\n" +
                "        <country>US</country>\n" +
                "    </address>\n" +
                "    <electronicMailAddress>jdeck@berkeley.edu</electronicMailAddress>\n" +
                "</creator>\n" +
                "<metadataProvider>\n" +
                "    <individualName>\n" +
                "        <givenName>Chris</givenName>\n" +
                "      <surName>Meyer</surName>\n" +
                "    </individualName>\n" +
                "    <organizationName>Smithsonian Institution National Museum of Natural History</organizationName>\n" +
                "    <positionName>Collections Manager</positionName>\n" +
                "    <address>\n" +
                "        <deliveryPoint></deliveryPoint>\n" +
                "        <city></city>\n" +
                "        <administrativeArea></administrativeArea>\n" +
                "        <postalCode></postalCode>\n" +
                "        <country>US</country>\n" +
                "    </address>\n" +
                "    <phone></phone>\n" +
                "    <electronicMailAddress>meyerc@si.edu</electronicMailAddress>\n" +
                "    <onlineUrl></onlineUrl>\n" +
                "</metadataProvider>\n" +
                "<pubDate>\n" +
                "      \t2016-06-19\n" +
                "</pubDate>\n" +
                "<language>eng</language>\n" +
                "<abstract>\n" +
                "  <para>Moorea Biocode Project</para>\n" +
                "</abstract>\n" +
                "      <keywordSet>\n" +
                "            <keyword>Specimen</keyword>\n" +
                "        <keywordThesaurus>GBIF Dataset Subtype Vocabulary: http://rs.gbif.org/vocabulary/gbif/dataset_subtype.xml</keywordThesaurus>\n" +
                "      </keywordSet>\n" +
                "      <keywordSet>\n" +
                "            <keyword>Occurrence</keyword>\n" +
                "        <keywordThesaurus>GBIF Dataset Type Vocabulary: http://rs.gbif.org/vocabulary/gbif/dataset_type.xml</keywordThesaurus>\n" +
                "      </keywordSet>\n" +
                "  <additionalInfo>\n" +
                "    <para>Please see http://vertnet.org/resources/norms.html for additional usage information.</para>\n" +
                "  </additionalInfo>\n" +
                "  <intellectualRights>\n" +
                "    <para>This work is licensed under a Creative Commons CCZero 1.0 License http://creativecommons.org/publicdomain/zero/1.0/legalcode.</para>\n" +
                "  </intellectualRights>\n" +
                "  <distribution scope=\"document\">\n" +
                "    <online>\n" +
                "      <url function=\"information\">http://biocode.berkeley.edu</url>\n" +
                "    </online>\n" +
                "  </distribution>\n" +
                "  <coverage>\n" +
                "      <geographicCoverage>\n" +
                "          <geographicDescription>Moorea, French Polynesia</geographicDescription>\n" +
                "        <boundingCoordinates>\n" +
                "          <westBoundingCoordinate>-161</westBoundingCoordinate>\n" +
                "          <eastBoundingCoordinate>-132</eastBoundingCoordinate>\n" +
                "          <northBoundingCoordinate>-3</northBoundingCoordinate>\n" +
                "          <southBoundingCoordinate>-27</southBoundingCoordinate>\n" +
                "        </boundingCoordinates>\n" +
                "      </geographicCoverage>\n" +
                "  </coverage>\n" +
                "  <contact>\n" +
                "    <individualName>\n" +
                "        <givenName>John</givenName>\n" +
                "      <surName>Deck</surName>\n" +
                "    </individualName>\n" +
                "    <organizationName>Berkeley Natural History Museums</organizationName>\n" +
                "    <positionName></positionName>\n" +
                "    <address>\n" +
                "        <deliveryPoint></deliveryPoint>\n" +
                "        <city></city>\n" +
                "        <administrativeArea></administrativeArea>\n" +
                "        <postalCode></postalCode>\n" +
                "        <country></country>\n" +
                "    </address>\n" +
                "    <phone></phone>\n" +
                "    <electronicMailAddress>jdeck@berkeley.edu</electronicMailAddress>\n" +
                "    <onlineUrl></onlineUrl>\n" +
                "  </contact>\n" +
                "</dataset>\n" +
                "</eml:eml>";
    }
}