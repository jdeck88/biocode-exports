package exports;


import org.omg.CORBA.portable.OutputStream;
import utils.biocodeExportsFileOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Export Biocode Project to ISA Tab
 */
public class isaTab extends exportConnector {

    String studyName = "s_MooreaBiocode.txt";
    String investigationName = "i_MooreaBiocode.txt";

    static File studyFile;

    public isaTab() throws Exception {
        super("isaTab");

        studyFile = new File(this.processDirectory.getAbsoluteFile().toString() + File.separatorChar + studyName);
    }

    /**
     * A sample method
     *
     * @throws SQLException
     */
    public String createStudy(File file) throws SQLException, IOException {
        biocodeExportsFileOutputStream befo = new biocodeExportsFileOutputStream(file);
        Statement stmt = conn.createStatement();
        String sql = "SELECT bnhm_id " +
                " FROM biocode " +
                " LIMIT 10";
        // some change
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            try {
                befo.write(rs.getString("bnhm_id") + "\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        befo.close();
        return file.getAbsoluteFile().toString();
    }

    /**
     * All export implementations run from their own main methods
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        isaTab isaTab = new isaTab();

        // createStudy and return path to output
        System.out.println(isaTab.createStudy(studyFile));

        //File investigationFile = new File (outputPath + File.pathSeparatorChar + investigationName);

    }
}
