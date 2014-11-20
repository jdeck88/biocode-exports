package exports;


import utils.biocodeExportsFileOutputStream;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Export Biocode Project to ISA Tab
 */
public class dwcArchiveExporter extends connector {

    String metafileName = "meta.xml";
    String sampleCoreName = "materialSample.txt";

    static File metaFile, sampleCoreFile, assayFile,investigationFile;

    public dwcArchiveExporter() throws Exception {
        super("dwca");

        String tmpDirName = this.processDirectory.getAbsoluteFile().toString();

        metaFile = new File(tmpDirName + File.separatorChar + metafileName);
        sampleCoreFile = new File(tmpDirName + File.separatorChar + sampleCoreName);

    }

    /**
     * create our ISA Study
     *
     * @throws java.sql.SQLException
     */
    public String createStudy() throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String sql =
                "SELECT\n" +
                        "\tbiocode.bnhm_id as 'Source Name'" +

                        // Characteristics of the source/specimen
                        // information intrinsic to the material (colour, location of sampling, mass)
                        ",\n\tbiocode_collecting_event.locality as 'Characteristics[locality]'" +
                        ",\n\tbiocode_collecting_event.Collector_List as 'Characteristics[Collector_List]'" +
                        ",\n\tbiocode_collecting_event.YearCollected as 'Characteristics[YearCollected]'" +
                        ",\n\tbiocode_collecting_event.MonthCollected as 'Characteristics[MonthCollected]'" +
                        ",\n\tbiocode_collecting_event.DayCollected as 'Characteristics[DayCollected]'" +
                        ",\n\tbiocode_collecting_event.TimeofDay as 'Characteristics[TimeofDay]'" +
                        ",\n\tbiocode_collecting_event.DecimalLatitude as 'Characteristics[DecimalLatitude]'" +
                        ",\n\tbiocode_collecting_event.DecimalLongitude as 'Characteristics[DecimalLongitude]'" +

                        ",\n\tbiocode.HoldingInstitution as 'Characteristics[Specimen HoldingInstitution]'" +
                        ",\n\tbiocode.Weight as 'Characteristics[Weight]'" +
                        ",\n\tbiocode.WeightUnits as 'Unit'" +
                        ",\n\tbiocode.Length as 'Characteristics[Length]'" +
                        ",\n\tbiocode.LengthUnits as 'Unit'" +

                        // Parameters of the source/specimen
                        // how something has been processed but not really intrinsic to the material.
                        ",\n\t'sample collection' as 'Protocol REF'" +
                        ",\n\tbiocode_tissue.preservative as 'Parameter Value[preservative]'" +
                        ",\n\tbiocode_tissue.container as 'Parameter Value[container]'" +

                        // Items pertaining to the sample (or tissue)
                        ",\n\tconcat(biocode.bnhm_id,'.',t.tissue_num) as 'Sample Name'" +
                        ",\n\tbiocode_tissue.tissuetype as 'Characteristics[tissuetype]'" +
                        ",\n\tbiocode_tissue.HoldingInstitution as 'Characteristics[TissueHoldingInstitution]'" +

                        "\nFROM biocode, biocode_collecting_event, biocode_tissue" +

                        // Where
                        "\nWHERE" +
                        "\n\tbiocode.coll_eventID = biocode_collecting_event.eventid" +
                        " &&\n\tbiocode.bnhm_id = biocode_tissue.bnhm_id\n" +
                        // Remove this line when i'm done testing
                        " &&\n\t (biocode.bnhm_id = 'MBIO56' || biocode.bnhm_id = 'MBIO2541')";
    /*
 `seq_num` varchar(40) NOT NULL DEFAULT '',
  `DateFirstEntered` date DEFAULT NULL,
  `EnteredBy` varchar(128) DEFAULT NULL,
  `DateLastModified` date DEFAULT NULL,
  `ModifiedBy` varchar(128) DEFAULT NULL,
  `ModifyReason` varchar(255) DEFAULT NULL,
  `ProjectCode` varchar(4) DEFAULT NULL,
  `OrigInstitution` varchar(255) DEFAULT NULL,
  `Specimen_Num_Collector` varchar(128) DEFAULT NULL,
  `CatalogNumberNumeric` int(10) unsigned DEFAULT NULL,
  `AccessionNumber` varchar(100) DEFAULT NULL,
  `VerbatimCollectingLabel` varchar(255) DEFAULT NULL,
  `VerbatimIDLabel` varchar(255) DEFAULT NULL,
  `CollectingLabelNotes` varchar(255) DEFAULT NULL,
  `specimen_ElevationMeters` decimal(10,4) DEFAULT NULL,
  `specimen_MinDepthMeters` decimal(10,4) DEFAULT NULL,
  `specimen_MaxDepthMeters` decimal(10,4) DEFAULT NULL,
  `ScientificName` varchar(255) DEFAULT NULL,
  `ColloquialName` varchar(255) DEFAULT NULL,
  `Kingdom` varchar(50) DEFAULT NULL,
  `Phylum` varchar(50) DEFAULT NULL,
  `Subphylum` varchar(50) DEFAULT NULL,
  `Superclass` varchar(50) DEFAULT NULL,
  `Class` varchar(50) DEFAULT NULL,
  `Subclass` varchar(50) DEFAULT NULL,
  `Infraclass` varchar(50) DEFAULT NULL,
  `Superorder` varchar(50) DEFAULT NULL,
  `Ordr` varchar(50) DEFAULT NULL,
  `Suborder` varchar(50) DEFAULT NULL,
  `Infraorder` varchar(50) DEFAULT NULL,
  `Superfamily` varchar(50) DEFAULT NULL,
  `Family` varchar(50) DEFAULT NULL,
  `Subfamily` varchar(50) DEFAULT NULL,
  `Tribe` varchar(50) DEFAULT NULL,
  `Subtribe` varchar(50) DEFAULT NULL,
  `Genus` varchar(50) DEFAULT NULL,
  `Subgenus` varchar(50) DEFAULT NULL,
  `SpecificEpithet` varchar(50) DEFAULT NULL,
  `SubspecificEpithet` varchar(128) DEFAULT NULL,
  `ScientificNameAuthor` varchar(255) DEFAULT NULL,
  `MorphoSpecies_Match` varchar(255) DEFAULT NULL,
  `MorphoSpecies_Description` varchar(255) DEFAULT NULL,
  `LowestTaxon` varchar(255) DEFAULT NULL,
  `LowestTaxonLevel` varchar(128) DEFAULT NULL,
  `IdentifiedBy` varchar(255) DEFAULT NULL,
  `IdentifiedInstitution` varchar(255) DEFAULT NULL,
  `BasisOfID` varchar(255) DEFAULT NULL,
  `YearIdentified` mediumint(8) unsigned DEFAULT NULL,
  `MonthIdentified` tinyint(3) unsigned DEFAULT NULL,
  `DayIdentified` tinyint(3) unsigned DEFAULT NULL,
  `PreviousID` varchar(255) DEFAULT NULL,
  `TypeStatus` varchar(255) DEFAULT NULL,
  `SexCaste` varchar(50) DEFAULT NULL,
  `LifeStage` varchar(50) DEFAULT NULL,
  `Parts` varchar(255) DEFAULT NULL,
  `Weight` decimal(10,4) DEFAULT NULL,
  `WeightUnits` varchar(10) DEFAULT NULL,
  `Length` decimal(10,4) DEFAULT NULL,
  `LengthUnits` varchar(10) DEFAULT NULL,
  `PreparationType` varchar(255) DEFAULT NULL,
  `preservative` varchar(128) DEFAULT NULL,
  `fixative` varchar(128) DEFAULT NULL,
  `relaxant` varchar(128) DEFAULT NULL,
  `IndividualCount` varchar(50) DEFAULT NULL,
  `specimen_Habitat` varchar(255) DEFAULT NULL,
  `specimen_MicroHabitat` varchar(255) DEFAULT NULL,
  `Associated_Taxon` varchar(255) DEFAULT NULL,
  `Cultivated` varchar(3) DEFAULT NULL,
  `association_type` varchar(255) DEFAULT NULL,
  `color` varchar(128) DEFAULT NULL,
  `VoucherCatalogNumber` varchar(128) DEFAULT NULL,
  `Voucher_URI` varchar(255) DEFAULT NULL,
  `RelatedCatalogItem` varchar(128) DEFAULT NULL,
  `PublicAccess` varchar(50) DEFAULT NULL,
  `Notes` text,
  `pic` varchar(255) DEFAULT NULL,
  `bnhm_id` char(24) DEFAULT NULL,
  `record_source` varchar(255) DEFAULT NULL,
  `dl_notes` varchar(255) DEFAULT NULL,
  `DNASequenceNo` varchar(255) DEFAULT NULL,
  `RecheckFlag` tinyint(3) unsigned DEFAULT NULL,
  `HoldingInstitution` varchar(255) DEFAULT NULL,
  `Coll_EventID` int(11) DEFAULT NULL,
  `Taxon_Certainty` varchar(48) DEFAULT NULL,
  `Tissue` tinyint(3) unsigned DEFAULT NULL,
  `namesoup` varchar(255) DEFAULT NULL,
  `LowestTaxon_Generated` varchar(255) DEFAULT NULL,
  `parent_record` char(24) DEFAULT NULL,
  `child_exists` tinyint(3) unsigned DEFAULT NULL,
  `batch_id` char(24) DEFAULT NULL,
  `SubProject` varchar(255) DEFAULT NULL,
  `SubSubProject` varchar(255) DEFAULT NULL,
  `guid` varchar(120) DEFAULT NULL,
  `taxonguid` varchar(120) DEFAULT NULL,
  */


        System.out.println(sql);
        return writeResultSet(stmt.executeQuery(sql), sampleCoreFile);
    }

    /**
     * Create our ISA assay (only 1 for nucleic acid sequencing)
     *
     * @return
     * @throws java.sql.SQLException
     * @throws java.io.IOException
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
        dwcArchiveExporter isaTab = new dwcArchiveExporter();

        // createStudy and return path to output
        System.out.println(isaTab.createStudy());
        System.out.println(isaTab.createAssay());
        System.out.println(isaTab.createInvestigation());


        //File investigationFile = new File (outputPath + File.pathSeparatorChar + investigationName);

    }

    /**
     * Hard-code the investigation output here
     * @return
     * @throws java.io.IOException
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
                "Study Assay Measurement Type\tsequencing\t\t\t\t\n" +
                "Study Assay Measurement Type Term Accession Number\t\t\t\t\t\n" +
                "Study Assay Measurement Type Term Source REF\t\t\t\t\t\n" +
                "Study Assay Technology Type\t\t\t\t\t\t\t\n" +
                "Study Assay Technology Type Term Accession Number\t\t\t\t\t\n" +
                "Study Assay Technology Type Term Source REF\t\t\t\t\t\n" +
                "Study Assay Technology Platform\t\t\t\t\t\n" +
                "Study Assay File Name\ta_sequencing.txt\t\t\t\t\n" +
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
