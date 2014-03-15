package exports;


import org.omg.CORBA.portable.OutputStream;
import utils.biocodeExportsFileOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Export Biocode Project to ISA Tab
 */
public class isaTab extends exportConnector {

    String investigationName = "i_biocodeProject.txt";
    String studyName = "s_mooreaBiocode.txt";
    String assayName = "a_sequencing.txt";

    static File investigationFile, studyFile, assayFile;

    public isaTab() throws Exception {
        super("isaTab");

        String tmpDirName = this.processDirectory.getAbsoluteFile().toString();

        investigationFile = new File(tmpDirName + File.separatorChar + investigationName);
        studyFile = new File(tmpDirName + File.separatorChar + studyName);
        assayFile = new File(tmpDirName + File.separatorChar + assayName);

    }

    /**
     * create our ISA Study
     *
     * @throws SQLException
     */
    public String createStudy() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "SELECT\n" +
                        "\tb.bnhm_id as 'Source Name',\n" +
                        "\tconcat(b.bnhm_id,'.',t.tissue_num) as 'Sample Name',\n" +

                        "\te.locality as 'Characteristics[locality]',\n" +
                        "\te.Collector_List as 'Characteristics[Collector_List]',\n" +
                        "\te.YearCollected as 'Characteristics[YearCollected]',\n" +
                        "\te.MonthCollected as 'Characteristics[MonthCollected]',\n" +
                        "\te.DayCollected as 'Characteristics[DayCollected]',\n" +
                        "\te.TimeofDay as 'Characteristics[TimeofDay]',\n" +
                        "\te.DecimalLatitude as 'Characteristics[DecimalLatitude]',\n" +
                        "\te.DecimalLongitude as 'Characteristics[DecimalLongitude]',\n" +

                        "\tb.HoldingInstitution as 'Characteristics[Specimen HoldingInstitution]',\n" +
                        "\tb.Weight as 'Characteristics[Weight]',\n" +
                        "\tb.WeightUnits as 'Unit',\n" +
                        "\tb.Length as 'Characteristics[Length]',\n" +
                        "\tb.LengthUnits as 'Unit',\n" +

                        "\tt.preservative as 'Characteristics[preservative]',\n" +
                        "\tt.tissuetype as 'Characteristics[tissuetype]',\n" +
                        "\tt.container as 'Characteristics[container]',\n" +
                        "\tt.HoldingInstitution as 'Characteristics[TissueHoldingInstitution]'\n" +

                        "FROM biocode b, biocode_collecting_event e, biocode_tissue t\n" +

                        "WHERE\n" +
                        "\tb.coll_eventID = e.eventid &&\n" +
                        "\tb.bnhm_id = t.bnhm_id\n" +

                        // Remove this line when i'm done testing
                        "\t && (b.bnhm_id = 'MBIO56' || b.bnhm_id = 'MBIO2541')\n";

        //System.out.println(sql);
        return writeResultSet(stmt.executeQuery(sql), studyFile);
    }

    /**
     * Create our ISA assay (only 1 for nucleic acid sequencing)
     *
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public String createAssay() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT\n" +
                "\textraction.sampleId as 'Sample Name',\n" +

                "\tworkflow.locus as 'Characteristics[locus]',\n" +
                "\tassembly.consensus as 'Characteristics[consensus sequence]',\n" +

                "\textraction.method as 'Characteristics[extraction method]',\n" +
                "\textraction.volume as 'Characteristics[extraction volume]',\n" +
                "\textraction.dilution as 'Characteristics[extraction dilution]'\n" +

                // From statement is complex due to inner join to find most RECENT assembly only
                "FROM workflow, extraction, assembly inner join \n" +
                "\t(SELECT extraction_id,max(date) as latest_date\n" +
                "\tFROM assembly\n" +
                "\tWHERE\n" +
                "\t\textraction_id LIKE 'MBIO%' &&\n" +
                "\t\tprogress='passed'\n" +

                // Remove this line when i'm done testing
                "\t\t&& (extraction_id LIKE 'MBIO56.%' || extraction_id LIKE 'MBIO2541.%') \n" +

                "\tGROUP BY extraction_id) A " +
                "\tON assembly.extraction_id = A.extraction_id AND assembly.date = A.latest_date\n" +
                "WHERE\n" +
                "\tA.extraction_id = extraction.extractionId &&\n" +
                "\tworkflow.id = assembly.workflow";

        //System.out.println(sql);
        return writeResultSet(stmt.executeQuery(sql), assayFile);
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
        System.out.println(isaTab.createStudy());
        System.out.println(isaTab.createAssay());
        System.out.println(isaTab.createInvestigation());


        //File investigationFile = new File (outputPath + File.pathSeparatorChar + investigationName);

    }

    /**
     * Hard-code the investigation output here
     * @return
     * @throws IOException
     */
    public String createInvestigation() throws IOException {
        biocodeExportsFileOutputStream befo = new biocodeExportsFileOutputStream(investigationFile);

        String investigationString = "ONTOLOGY SOURCE REFERENCE\t\t\t\t\t\t\t\n" +
                "Term Source Name\tUO\tOBI\tCHEBI\tPATO\tNCBITax\t\tENVO\n" +
                "Term Source File\t\t\t\t\t\t\t\n" +
                "Term Source Version\tv 1.26\tbeta\tv 1.26\tv 1.26\tv 1.26\t\tv 1.26\n" +
                "Term Source Description\tUnit Ontology\tOntology of Biomedical Investigation\tChemical Entities of Biological Interest\tPhenotypic qualities (properties)\tNCBI Taxonomy Database\t\tEnvironment Ontology\n" +
                "INVESTIGATION\t\t\t\t\t\t\t\n" +
                "Investigation Identifier\t\t\t\t\t\t\t\n" +
                "Investigation Title\t\t\t\t\t\t\t\n" +
                "Investigation Description\t\t\t\t\t\t\t\n" +
                "Investigation Submission Date\t\t\t\t\t\t\t\n" +
                "Investigation Public Release Date\t\t\t\t\t\t\t\n" +
                "INVESTIGATION PUBLICATIONS\t\t\t\t\t\t\t\n" +
                "Investigation PubMed ID\t\t\t\t\t\t\t\n" +
                "Investigation Publication DOI\t\t\t\t\t\t\t\n" +
                "Investigation Publication Author list\t\t\t\t\t\t\t\n" +
                "Investigation Publication Title\t\t\t\t\t\t\t\n" +
                "Investigation Publication Status\t\t\t\t\t\t\t\n" +
                "Investigation Publication Status Term Accession Number\t\t\t\t\t\t\t\n" +
                "Investigation Publication Status Term Source REF\t\t\t\t\t\t\t\n" +
                "INVESTIGATION CONTACTS\t\t\t\t\t\t\t\n" +
                "Investigation Person Last Name\t\t\t\t\t\t\t\n" +
                "Investigation Person First Name\t\t\t\t\t\t\t\n" +
                "Investigation Person Mid Initials\t\t\t\t\t\t\t\n" +
                "Investigation Person Email\t\t\t\t\t\t\t\n" +
                "Investigation Person Phone\t\t\t\t\t\t\t\n" +
                "Investigation Person Fax\t\t\t\t\t\t\t\n" +
                "Investigation Person Address\t\t\t\t\t\t\t\n" +
                "Investigation Person Affiliation\t\t\t\t\t\t\t\n" +
                "Investigation Person Roles\t\t\t\t\t\t\t\n" +
                "Investigation Person Roles Term Accession Number\t\t\t\t\t\t\t\n" +
                "Investigation Person Roles Term Source REF\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\n" +
                "STUDY\t\t\t\t\t\t\t\n" +
                "Study Identifier\tMooreaBiocode\t\t\t\t\t\t\n" +
                "Study Title\tMoorea Biocode Project\t\t\t\t\t\t\n" +
                "Study Submission Date\t1/4/14\t\t\t\t\t\t\n" +
                "Study Public Release Date\t1/4/14\t\t\t\t\t\t\n" +
                "Study Description\tMarker Gene Sequencing of all macrobiota on a tropical island\t\t\t\t\t\t\n" +
                "Study File Name\ts_MooreaBiocode.txt\t\t\t\t\t\t\n" +
                "STUDY DESIGN DESCRIPTORS\t\t\t\t\t\t\t\n" +
                "Study Design Type\t\t\t\t\t\t\t\n" +
                "Study Design Type Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Design Type Term Source REF\t\t\t\t\t\t\t\n" +
                "STUDY PUBLICATIONS\t\t\t\t\t\t\t\n" +
                "Study PubMed ID\t\t\t\t\t\t\t\n" +
                "Study Publication DOI\tdoi:10.7286/V1H41PBN?\tdoi:10.7286/V1MW2F22?\tdoi:10.7286/V1154F0D?\t\t\t\t\n" +
                "Study Publication Author list\t\t\t\t\t\t\t\n" +
                "Study Publication Title\t\t\t\t\t\t\t\n" +
                "Study Publication Status\t\t\t\t\t\t\t\n" +
                "Study Publication Status Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Publication Status Term Source REF\t\t\t\t\t\t\t\n" +
                "STUDY FACTORS\t\t\t\t\t\t\t\n" +
                "Study Factor Name\t\t\t\t\t\t\t\n" +
                "Study Factor Type\t\t\t\t\t\t\t\n" +
                "Study Factor Type Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Factor Type Term Source REF\t\t\t\t\t\t\t\n" +
                "STUDY ASSAYS\t\t\t\t\t\t\t\n" +
                "Study Assay Measurement Type\tspecimenPreservation\ttissueSampling\tsequencing\t\t\t\t\n" +
                "Study Assay Measurement Type Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Assay Measurement Type Term Source REF\t\t\t\t\t\t\t\n" +
                "Study Assay Technology Type\t\t\t\t\t\t\t\n" +
                "Study Assay Technology Type Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Assay Technology Type Term Source REF\t\t\t\t\t\t\t\n" +
                "Study Assay Technology Platform\t\t\t\t\t\t\t\n" +
                "Study Assay File Name\ta_specimenPreservation.txt\ta_tissueSampling.txt\ta_sequencing.txt\t\t\t\t\n" +
                "STUDY PROTOCOLS\t\t\t\t\t\t\t\n" +
                "Study Protocol Name\tcollectingEvent\tpreservingSpecimen\tdestructiveTissue\tpreservingTissue\tdnaExtraction\tpcr\tsequencing\n" +
                "Study Protocol Type\tenvironmental material collection\t\tnucleic acid extraction\tRNA extraction\tDNA extraction\t\tnucleic acid sequencing\n" +
                "Study Protocol Type Term Accession Number\t\t\t\t\t\t\t626\n" +
                "Study Protocol Type Term Source REF\t\t\t\t\t\t\tOBI\n" +
                "Study Protocol Description\t\t\t\t\t\t\t\n" +
                "Study Protocol URI\t\t\t\t\t\t\t\n" +
                "Study Protocol Version\t\t\t\t\t\t\t\n" +
                "Study Protocol Parameters Name\t\t\t\t\t\t\tsequencing instrument\n" +
                "Study Protocol Parameters Name Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Protocol Parameters Name Term Source REF\t\t\t\t\t\t\t\n" +
                "Study Protocol Components Name\t\t\t\t\t\t\t\n" +
                "Study Protocol Components Type\t\t\t\t\t\t\tDNA sequencer\n" +
                "Study Protocol Components Type Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Protocol Components Type Term Source REF\t\t\t\t\t\t\t\n" +
                "STUDY CONTACTS\t\t\t\t\t\t\t\n" +
                "Study Person Last Name\tDavies\tMeyer\tDeck\t\t\t\t\n" +
                "Study Person First Name\tNeil\tChristopher\tJohn\t\t\t\t\n" +
                "Study Person Mid Initials\t\t\t\t\t\t\t\n" +
                "Study Person Email\t\t\t\t\t\t\t\n" +
                "Study Person Phone\t\t\t\t\t\t\t\n" +
                "Study Person Fax\t\t\t\t\t\t\t\n" +
                "Study Person Address\t\t\t\t\t\t\t\n" +
                "Study Person Affiliation\t\t\t\t\t\t\t\n" +
                "Study Person Roles\t\t\t\t\t\t\t\n" +
                "Study Person Roles Term Accession Number\t\t\t\t\t\t\t\n" +
                "Study Person Roles Term Source REF";
        befo.write(investigationString);
        befo.close();
        return investigationFile.getAbsoluteFile().toString();
    }
}
