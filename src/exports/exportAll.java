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
public class exportAll extends connector {
    static final int BUFFER = 2048;

    static File occurrenceDataFile;
    String tmpDirName;

    public exportAll (String processDirectory) throws Exception {
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
    public String dumpCollectingEvents() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql = "select * FROM biocode_collecting_event";

        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Collecting_Events.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }
    public String dumpTissues() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql = "select  * FROM biocode_tissue"; 

        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Tissues.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }

    public String dumpSpecimens() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql = "select  * FROM biocode";

        occurrenceDataFile = new File(tmpDirName + File.separatorChar + "Specimens.txt");
        return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
    }

    public static void main(String[] args) throws Exception {
        // Some classes to help us
        CommandLineParser clp = new GnuParser();
        HelpFormatter helpf = new HelpFormatter();
        CommandLine cl;

        // The input file
        String outputDirectory = null;

        // Define our commandline options
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message and exit");
        options.addOption("o", "outputDirectory", true, "Output Directory");

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
            helpf.printHelp("exportAll ", options, true);
            return;
        }

        if (cl.hasOption("h")) {
            helpf.printHelp("exportAll ", options, true);
            return;
        }
        if (cl.hasOption("o")) {
            outputDirectory = cl.getOptionValue("o");
        }

        // Dump Data
        exportAll d = new exportAll(outputDirectory);
        d.dumpSpecimens();
        d.dumpCollectingEvents();
        d.dumpTissues();

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
