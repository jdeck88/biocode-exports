package exports;


import utils.biocodeExportsFileOutputStream;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Export Biocode Project to Darwin Core Archive
 */
public class dumpUniqueNames extends connector {

    File uniqueNameFile;
    String tmpDirName;


    public dumpUniqueNames() throws Exception {
        super("names");

        tmpDirName = this.processDirectory.getAbsoluteFile().toString();
    }

    /**
     * Dump Names
     *
     * @throws java.sql.SQLException
     */
    public String dumpProjectCode(String projectCode) throws SQLException, IOException {
        //projectCode = "MBIOINDO";
        Statement stmt = conn.createStatement();
        String sql =
                "SELECT\n" +
                        "\tcount(*) as count,\n" +
                        "\tScientificName,\n" +
                        "\tColloquialName,\n" +
                        "\tKingdom,\n" +
                        "\tPhylum,\n" +
                        "\tSubphylum,\n" +
                        "\tSuperclass,\n" +
                        "\tClass,\n" +
                        "\tSubclass,\n" +
                        "\tInfraclass,\n" +
                        "\tSuperorder,\n" +
                        "\tOrdr,\n" +
                        "\tSuborder,\n" +
                        "\tInfraorder,\n" +
                        "\tSuperfamily,\n" +
                        "\tFamily,\n" +
                        "\tSubfamily,\n" +
                        "\tTribe,\n" +
                        "\tSubtribe,\n" +
                        "\tGenus,\n" +
                        "\tSubgenus,\n" +
                        "\tSpecificEpithet,\n" +
                        "\tSubspecificEpithet,\n" +
                        "\tScientificNameAuthor,\n" +
                        "\tLowestTaxon,\n" +
                        "\tLowestTaxonLevel\n" +
                        "\nFROM biocode, biocode_collecting_event " +
                        // Where
                        "\nWHERE" +
                        "\n\tbiocode.coll_eventID = biocode_collecting_event.eventid" +
                        " &&\n\t biocode_collecting_event.projectcode = '" + projectCode + "'" +
                        //" &&\n\t (biocode_collecting_event.projectcode = 'MBIO'" +
                        //" || biocode_collecting_event.projectcode = 'INDO')" +
                        "\nGROUP BY " +
                        "\tScientificName,\n" +
                        "\tColloquialName,\n" +
                        "\tKingdom,\n" +
                        "\tPhylum,\n" +
                        "\tSubphylum,\n" +
                        "\tSuperclass,\n" +
                        "\tClass,\n" +
                        "\tSubclass,\n" +
                        "\tInfraclass,\n" +
                        "\tSuperorder,\n" +
                        "\tOrdr,\n" +
                        "\tSuborder,\n" +
                        "\tInfraorder,\n" +
                        "\tSuperfamily,\n" +
                        "\tFamily,\n" +
                        "\tSubfamily,\n" +
                        "\tTribe,\n" +
                        "\tSubtribe,\n" +
                        "\tGenus,\n" +
                        "\tSubgenus,\n" +
                        "\tSpecificEpithet,\n" +
                        "\tSubspecificEpithet,\n" +
                        "\tScientificNameAuthor,\n" +
                        "\tLowestTaxon,\n" +
                        "\tLowestTaxonLevel\n";


        String outputDir = tmpDirName + File.separatorChar + projectCode + ".txt";

        System.out.println("Writing output to " + outputDir);

        uniqueNameFile = new File(tmpDirName + File.separatorChar + projectCode + ".txt");

        return writeResultSet(stmt.executeQuery(sql), uniqueNameFile);
    }

    public static void main(String[] args) throws Exception {
        dumpUniqueNames d = new dumpUniqueNames();
        d.dumpProjectCode("MBIO");
        d.dumpProjectCode("INDO");
    }
}