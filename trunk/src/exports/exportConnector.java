package exports;

import utils.PathManager;
import utils.SettingsManager;
import utils.database;

import java.awt.geom.Path2D;
import java.io.File;
import java.sql.Connection;

/**
 * Super-class for connecting to the Biocode Databzse
 */
public class exportConnector {
    protected Connection conn;
    private SettingsManager sm;
    File processDirectory;
    PathManager pm;
    String exportDir = "biocode-export";

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
}
