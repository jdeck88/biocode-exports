package updates;

import org.apache.commons.cli.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jdeck on 6/23/16.
 */
public class sylvainUpdater {

    public static void main(String[] args) throws Exception {

               // Some classes to help us
        CommandLineParser clp = new GnuParser();
        HelpFormatter helpf = new HelpFormatter();
        CommandLine cl;

        // The input file
        String inputFileName = null;


        // Define our commandline options
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message and exit");
        options.addOption("i", "input", true, "Input FileName");


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
        if (cl.getOptions().length < 1) {
            helpf.printHelp("sylvainUpdater ", options, true);
            return;
        }

        if (cl.hasOption("h")) {
            helpf.printHelp("sylvainUpdater ", options, true);
            return;
        }
        if (cl.hasOption("i")) {
            inputFileName = cl.getOptionValue("i");
        }

        // Splitting data into three parts since the entire spreadsheet was too big for POI to read at once.
        runUpdate(new File(inputFileName), "Sheet1");
        //runUpdate(new File("/Users/jdeck/IdeaProjects/biocode-exports/biocode-exports/data/symbiocode_part1.xlsx"), "Sheet1");
        //runUpdate(new File("/Users/jdeck/IdeaProjects/biocode-exports/biocode-exports/data/symbiocode_part2.xlsx"), "Sheet1");
        //runUpdate(new File("/Users/jdeck/IdeaProjects/biocode-exports/biocode-exports/data/symbiocode_part3.xlsx"), "Sheet1");
    }

    public static void runUpdate(File in, String nameOfSheet) throws Exception {
        database d = new database();
        d.getConn().setAutoCommit(false);

        XSSFWorkbook wb = new XSSFWorkbook(OPCPackage.open(in, PackageAccess.READ));

        // Set the sheet name to samples
        wb.setSheetName(0, nameOfSheet);

        // Adjust column names
        Sheet sheet = wb.getSheetAt(0);
        int rowInt = 0;

        while (rowInt < sheet.getLastRowNum()) {
            // increment to begin with because first row is headers
            rowInt++;
            // Get column headers row
            Row columnHeaders = sheet.getRow(0);
            // Get the data row
            Row row = sheet.getRow(rowInt);
            // Loop through all the columns in this row
            int columnInt = 0;
            String updateSpecimenString =
                    "UPDATE biocode.biocode set " +
                            "Phylum = ?," +
                            "Class = ?," +
                            "Ordr = ?," +
                            "Family = ?," +
                            "Genus = ?," +
                            "specificEpithet = ?," +
                            "IdentifiedBy = ?," +
                            "basisOfID = ?," +
                            "morphospecies_description = ?," +
                            "publicaccess = null," +
                            "Subphylum = null," +
                            "Superclass = null," +
                            "Subclass = null," +
                            "Infraclass = null," +
                            "Superorder = null," +
                            "Suborder = null," +
                            "Infraorder = null," +
                            "Superfamily = null," +
                            "Subfamily = null," +
                            "Tribe = null," +
                            "SubTribe = null," +
                            "Subgenus = null " +
                            "WHERE bnhm_id = ? " +
                            "and specimen_num_collector = ?";
            PreparedStatement updateSpecimen = d.getConn().prepareStatement(updateSpecimenString);

            String updateTissueString =
                    "UPDATE biocode.biocode_tissue set " +
                            "molecular_id = ?," +
                            "otherCatalogNum = ? " +
                            "WHERE bnhm_id = ? ";

            PreparedStatement updateTissue = d.getConn().prepareStatement(updateTissueString);
            ArrayList molecular_ids = new ArrayList();

            for (Cell columnName : columnHeaders) {

                String columnNameString = columnName.getStringCellValue();
                String cellValue = null;
                try {
                    cellValue = row.getCell(columnInt).getStringCellValue();
                } catch (NullPointerException npe) {
                    // do nothing, i think fine should be null to insert into db.
                }

                if (approxEquals(columnNameString, "phylum")) {
                    updateSpecimen.setString(1, cellValue);
                } else if (approxEquals(columnNameString, "Class")) {
                    updateSpecimen.setString(2, cellValue);
                } else if (approxEquals(columnNameString, "Order")) {
                    updateSpecimen.setString(3, cellValue);
                } else if (approxEquals(columnNameString, "Family")) {
                    updateSpecimen.setString(4, cellValue);
                } else if (approxEquals(columnNameString, "Genus")) {
                    updateSpecimen.setString(5, cellValue);
                } else if (approxEquals(columnNameString, "species")) {
                    updateSpecimen.setString(6, cellValue);
                } else if (approxEquals(columnNameString, "identifier")) {
                    updateSpecimen.setString(7, cellValue);
                } else if (approxEquals(columnNameString, "Identification method")) {
                    updateSpecimen.setString(8, cellValue);
                } else if (approxEquals(columnNameString, "Taxonomy Notes")) {
                    updateSpecimen.setString(9, cellValue);
                } else if (approxEquals(columnNameString, "biocode_id")) {
                    updateSpecimen.setString(10, cellValue);
                    updateTissue.setString(3, cellValue);
                } else if (approxEquals(columnNameString, "symbiocode_id")) {
                    updateSpecimen.setString(11, cellValue);
                } else if (approxEquals(columnNameString, "genbank_accession")) {
                    updateTissue.setString(1, cellValue);
                } else if (approxEquals(columnNameString, "bold_url")) {
                    updateTissue.setString(2, cellValue);
                }

                columnInt++;
            }

            // Print out our statements
            System.out.println(updateSpecimen.toString());
            System.out.println(updateTissue.toString());

            if (1==1) return;
            updateSpecimen.execute();

            updateTissue.execute();
            if (rowInt % 100 ==0 ) {
                System.out.println("100 rows updated from "+ in.getName() + ": " + rowInt + " out of 5000");
            }
        }

    }

    public static boolean approxEquals(String columnName, String value) {
        String cleanedColumnName = columnName.trim().replaceAll(" +", " ");
        if (cleanedColumnName.equalsIgnoreCase(value.trim())) return true;
        else return false;
    }

}
