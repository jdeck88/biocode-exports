package updates;

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

/**
 * Created by jdeck on 6/23/16.
 */
public class sylvainUpdater {
    public static void main(String[] args) throws Exception {
        runUpdate(new File("/Users/jdeck/IdeaProjects/biocode-exports/biocode-exports/data/symbiocode_part1.xlsx"), "Sheet1");
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
                            "species = ?," +
                            "IdentifiedBy = ?," +
                            "basisOfID = ?," +
                            "morphospecies_description = ?," +
                            "publicaccess = ?," +
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
                            "WHERE biocode_id = ? " +
                            "and specimen_num_collector = ?";
            PreparedStatement updateSpecimen = d.getConn().prepareStatement(updateSpecimenString);

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
                } else if (approxEquals(columnNameString, "specimen_available")) {
                    updateSpecimen.setString(10, cellValue);
                } else if (approxEquals(columnNameString, "biocode_id")) {
                    updateSpecimen.setString(11, cellValue);
                } else if (approxEquals(columnNameString, "symbiocode_id")) {
                    updateSpecimen.setString(12, cellValue);
                }

                columnInt++;
            }
            System.out.println(updateSpecimen.toString());
            return;
        }

    }

    public static boolean approxEquals(String columnName, String value) {
        String cleanedColumnName = columnName.trim().replaceAll(" +", " ");
        if (cleanedColumnName.equalsIgnoreCase(value.trim())) return true;
        else return false;
    }

}
