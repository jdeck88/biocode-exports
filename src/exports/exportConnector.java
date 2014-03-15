package exports;

import utils.PathManager;
import utils.SettingsManager;
import utils.biocodeExportsFileOutputStream;
import utils.database;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Super-class for connecting to the Moorea Biocode Database
 * This is meant to be extended by subClasses that have particular output formats.
 */
public class exportConnector {
    protected Connection conn;
    private SettingsManager sm;
    File processDirectory;
    PathManager pm;
    String exportDir = "biocode-export";

    /**
     * The constructor for this class takes a "subPath" designating where the output files for
     * the exports will live, e.g. "isaTab", "BOLD", "Merritt"
     * @param subPath
     * @throws Exception
     */
    public exportConnector(String subPath) throws Exception {
        database db = new database();
        conn = db.getConn();

        // Initialize settings manager
        sm = SettingsManager.getInstance();
        try {
            sm.loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set the processing directory
        pm = new PathManager();

        processDirectory = pm.setDirectory((
                System.getProperty("java.io.tmpdir")) +
                File.separatorChar +
                exportDir +
                File.separatorChar +
                subPath);
    }

    /**
     * Write a resultset to a file.  This function loops through all columns and tables and
     * assigns column headings to the first row of the text file and uses tab-delimited text output format.
     * @param rs
     * @param file
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String writeResultSet(ResultSet rs, File file) throws IOException, SQLException {
        biocodeExportsFileOutputStream befo = new biocodeExportsFileOutputStream(file);

        // Use column names as the header
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        // The column count starts from 1
        for (int i = 1; i < columnCount + 1; i++) {
            if (i > 1) befo.write("\t");
            befo.write(rsmd.getColumnLabel(i));
        }
        befo.write("\n");

        // Loop through the contents
        while (rs.next()) {
            for (int i = 1; i < columnCount + 1; i++) {
                if (i > 1) befo.write("\t");
                try {
                    befo.write(rs.getString(i));
                } catch (Exception e) {
                    befo.write("");
                }
            }
            befo.write("\n");
        }
        befo.close();

        return file.getAbsoluteFile().toString();
    }
}
