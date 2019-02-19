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
import java.util.HashMap;
import java.util.Map;

/**
 * Export for DROP project into Geome
 *
 * This class exports the DROP project into Geome.
 * Each year is going to be a unique expedition
 * select * from biocode_collecting_event where eventid not in 
 * (select coll_eventid from biocode where projectCode = 'INDO') and projectCode = 'INDO';
 */
public class exportDropForGeome extends connector {
	static final int BUFFER = 2048;

	static File occurrenceDataFile;
	String tmpDirName;

	public exportDropForGeome (String processDirectory) throws Exception {
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
	public String dumpCollectingEvents(String projectCode, String expedition, Integer year) throws SQLException, IOException {
		Statement stmt = conn.createStatement();
		String sql =
			"select \n" +
			"  character_sanitizer(e.Coll_EventID_collector) as eventID,\n" +
			"  e.Collection_Method as samplingProtocol,\n" +
			"  e.Collector_List as collectorList,\n" +
			"  e.ContinentOcean as continentOcean,\n" +
			"  e.Country as country,\n" +
			"  e.County as county,\n" +
			"  e.DecimalLatitude as decimalLatitude,\n" +
			"  e.DecimalLongitude as decimalLongitude,\n" +
			"  e.DepthOfBottomMeters as depthOfBottomInMeters,\n" +
			"  e.EnteredBy as enteredBy,\n" +
			"  e.Habitat as habitat,\n" +
			"  cast(e.HorizontalDatum as unsigned integer) as horizontalDatum,\n" +
			"  e.Island as island,\n" +
			"  e.IslandGroup as islandGroup,\n" +
			"  e.Landowner as landowner,\n" +
			"  e.Locality as locality,\n" +
			"  cast(e.MaxDepthMeters as unsigned integer) as maximumDepthInMeters,\n" +
			"  cast(e.MaxElevationMeters as unsigned integer) as maximumElevationInMeters,\n" +
			"  cast(e.MaxErrorInMeters as unsigned integer) as coordinateUncertaintyInMeters,\n" +
			"  cast(e.MinDepthMeters as unsigned integer) as minimumDepthInMeters,\n" +
			"  cast(e.MinElevationMeters as unsigned integer) as minimumElevationInMeters,\n" +
			"  e.MicroHabitat as microHabitat,\n" +
			"  e.Permit_Info as permitInformation,\n" +
			"  e.Remarks as eventRemarks,\n" +
			"  e.StateProvince as stateProvince,\n" +
			"  e.TaxTeam as taxTeam,\n" +
			"  e.TimeofDay as timeOfDay,\n" +
			"  e.VerbatimLatitude as verbatimLatitude,\n" +
			"  e.VerbatimLongitude as verbatimLongitude,\n" +
			"  e.YearCollected as yearCollected,\n" +
			"  e.MonthCollected as monthCollected,\n" +
			"  e.DayCollected as dayCollected\n" +
			"FROM  biocode b, biocode_collecting_event e \n" +
			"where b.Coll_EventID = e.EventID\n" +
			"AND (b.Specimen_Num_Collector like 'BCURA%' OR b.Specimen_Num_Collector like 'CURI%')\n" +
			"AND e.yearCollected=" + year +"\n";
		//sql += queryPrefixList(prefixList);
		sql += " group by character_sanitizer(e.Coll_EventID_collector)";
		occurrenceDataFile = new File(tmpDirName + File.separatorChar + projectCode + File.separatorChar + expedition+ "_Collecting_Events.txt");
		occurrenceDataFile.getParentFile().mkdirs();
		return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
	}

	/*
	    DEPRECATED
	   A function that gets called a few times here to assemble the prefixList query
	   this is a very INDO-specific function 
	   */
	public String queryPrefixList(String prefixList) {
		String sql = "";	
		sql += "AND (";

		String[] prefixListArray = prefixList.split(",");
		for (String prefix : prefixListArray) {
			sql += "b.specimen_num_collector like '" + prefix+ "%' OR ";
		}
		// Remove the last OR from the string (including two spaces)
		sql = sql.substring(0,sql.length() - 4);
		sql += ")";
		return sql;
	}

	public String dumpTissues(String projectCode,String expedition, Integer year) throws SQLException, IOException {
		Statement stmt = conn.createStatement();
		String sql =
			"select \n" +
			" character_sanitizer(b.Specimen_Num_Collector) as materialSampleID,\n" +
			" character_sanitizer(e.Coll_EventID_collector) as eventID,\n" +
			" b.Length as length,\n" +
			" b.LengthUnits as lengthUnits,\n" +
			" b.Weight as weight,\n" +
			" b.WeightUnits as weightUnits,\n" +
			" b.fixative as fixative,\n" +
			" b.HoldingInstitution as institutionCode,\n" +
			" b.IndividualCount as individualCount,\n" +
			" b.PreparationType as preparationType,\n" +
			" b.preservative as preservative,\n" +
			" b.relaxant as relaxant,\n" +
			" b.TypeStatus as typeStatus,\n" +
			" b.EnteredBy as enteredBy,\n" +
			" b.guid as catalogNumber,\n" +
			" b.ModifiedBy as modifiedBy,\n" +
			" b.ModifyReason as modifiedReason,\n" +
			" b.Notes as occurrenceRemarks,\n" +
			" b.RelatedCatalogItem as otherCatalogNumbers,\n" +
			" b.SubProject as subProject,\n" +
			" b.SubSubProject as subSubProject,\n" +
			" b.Voucher_URI as voucherURI,\n" +
			" b.VoucherCatalogNumber as voucherCatalogNumber,\n" +
			" b.Associated_Taxon as associatedTaxa,\n" +
			" b.association_type as organismRemarks,\n" +
			" b.BasisOfID as identificationRemarks,\n" +
			" b.Class as class,\n" +
			" b.ColloquialName as colloquialName,\n" +
			" b.Family as family,\n" +
			" b.Genus as genus,\n" +
			" b.IdentifiedBy as identifiedBy,\n" +
			" b.Infraclass as infraClass,\n" +
			" b.Infraorder as infraOrder,\n" +
			" b.Kingdom as kingdom,\n" +
			" b.LifeStage as lifeStage,\n" +
			" replace(b.LowestTaxonLevel,'ordr','order') as taxonRank,\n" +
			" b.MorphoSpecies_Description as morphospeciesDescription,\n" +
			" b.MorphoSpecies_Match as morphospeciesMatch,\n" +
			" b.Ordr as `order`,\n" +
			" b.Phylum as phylum,\n" +
			" b.PreviousID as previousIdentifications,\n" +
			" b.lowesttaxon_generated as scientificName,\n" +
			" b.SexCaste as sexCaste,\n" +
			" b.SpecificEpithet as specificEpithet,\n" +
			" b.Subclass as subClass,\n" +
			" b.Subfamily as subFamily,\n" +
			" b.Subgenus as subGenus,\n" +
			" b.Suborder as subOrder,\n" +
			" b.Subphylum as subPhylum,\n" +
			" b.SubspecificEpithet as infraspecificEpithet,\n" +
			" b.Subtribe as subTribe,\n" +
			" b.Superclass as superClass,\n" +
			" b.Superfamily as superFamily,\n" +
			" b.Superorder as superOrder,\n" +
			" b.Taxon_Certainty as identificationVerificationStatus,\n" +
			" b.Tribe as tribe,\n" +
			" b.YearIdentified as yearIdentified,\n" +
			" b.MonthIdentified as monthIdentified,\n" +
			" b.DayIdentified as dayIdentified,\n" +
			" character_sanitizer(concat(b.bnhm_id,'.',tissue_num)) as tissueID,\n" +
			" t.tissuetype as tissueType,\n" +
			" t.format_name96 as tissuePlate,\n" +
			" t.well_number96 as tissueWell,\n" +
			" t.guid as tissueCatalogNumber,\n" +
			" t.tissue_barcode as tissueBarcode,\n" +
			" t.HoldingInstitution as tissueInstitution,\n" +
			" t.OtherCatalogNum as tissueOtherCatalogNumbers,\n" +
			" t.year as tissueSamplingYear,\n" +
			" t.month as tissueSamplingMonth,\n" +
			" t.day as tissueSamplingDay,\n" +
			" t.person_subsampling as tissueRecordedBy,\n" +
			" t.container as tissueContainer,\n" +
			" t.preservative as tissuePreservative,\n" +
			" t.molecular_id as associatedSequences,\n" +
			" t.notes as tissueRemarks,\n" +
			" t.from_tissue as fromTissue\n" +
			"FROM biocode b, biocode_collecting_event e, biocode_tissue t\n" +
			"where b.Coll_EventID = e.EventID\n" +
			"AND b.bnhm_id=t.bnhm_id\n" + 
			"AND (b.Specimen_Num_Collector like 'BCURA%' OR b.Specimen_Num_Collector like 'CURI%')\n" +
			"AND e.yearCollected = " + year;

		occurrenceDataFile = new File(tmpDirName + File.separatorChar + projectCode + File.separatorChar + expedition + "_Specimens.txt");
		occurrenceDataFile.getParentFile().mkdirs();
		return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
	}

	public String dumpSpecimens(String projectCode, String expedition, Integer year) throws SQLException, IOException {
		Statement stmt = conn.createStatement();
		String sql =
			"select \n" +
			" character_sanitizer(b.Specimen_Num_Collector) as materialSampleID,\n" +
			" character_sanitizer(e.Coll_EventID_collector) as eventID,\n" +
			" b.Length as length,\n" +
			" b.LengthUnits as lengthUnits,\n" +
			" b.Weight as weight,\n" +
			" b.WeightUnits as weightUnits,\n" +
			" b.fixative as fixative,\n" +
			" b.HoldingInstitution as institutionCode,\n" +
			" b.IndividualCount as individualCount,\n" +
			" b.PreparationType as preparationType,\n" +
			" b.preservative as preservative,\n" +
			" b.relaxant as relaxant,\n" +
			" b.TypeStatus as typeStatus,\n" +
			" b.EnteredBy as enteredBy,\n" +
			" b.guid as catalogNumber,\n" +
			" b.ModifiedBy as modifiedBy,\n" +
			" b.ModifyReason as modifiedReason,\n" +
			" b.Notes as occurrenceRemarks,\n" +
			" b.RelatedCatalogItem as otherCatalogNumbers,\n" +
			" b.SubProject as subProject,\n" +
			" b.SubSubProject as subSubProject,\n" +
			" b.Voucher_URI as voucherURI,\n" +
			" b.VoucherCatalogNumber as voucherCatalogNumber,\n" +
			" b.Associated_Taxon as associatedTaxa,\n" +
			" b.association_type as organismRemarks,\n" +
			" b.BasisOfID as identificationRemarks,\n" +
			" b.Class as class,\n" +
			" b.ColloquialName as colloquialName,\n" +
			" b.Family as family,\n" +
			" b.Genus as genus,\n" +
			" b.IdentifiedBy as identifiedBy,\n" +
			" b.Infraclass as infraClass,\n" +
			" b.Infraorder as infraOrder,\n" +
			" b.Kingdom as kingdom,\n" +
			" b.LifeStage as lifeStage,\n" +
			" replace(b.LowestTaxonLevel,'ordr','order') as taxonRank,\n" +
			" b.MorphoSpecies_Description as morphospeciesDescription,\n" +
			" b.MorphoSpecies_Match as morphospeciesMatch,\n" +
			" b.Ordr as `order`,\n" +
			" b.Phylum as phylum,\n" +
			" b.PreviousID as previousIdentifications,\n" +
			" b.lowesttaxon_generated as scientificName,\n" +
			" b.SexCaste as sexCaste,\n" +
			" b.SpecificEpithet as specificEpithet,\n" +
			" b.Subclass as subClass,\n" +
			" b.Subfamily as subFamily,\n" +
			" b.Subgenus as subGenus,\n" +
			" b.Suborder as subOrder,\n" +
			" b.Subphylum as subPhylum,\n" +
			" b.SubspecificEpithet as infraspecificEpithet,\n" +
			" b.Subtribe as subTribe,\n" +
			" b.Superclass as superClass,\n" +
			" b.Superfamily as superFamily,\n" +
			" b.Superorder as superOrder,\n" +
			" b.Taxon_Certainty as identificationVerificationStatus,\n" +
			" b.Tribe as tribe,\n" +
			" b.YearIdentified as yearIdentified,\n" +
			" b.MonthIdentified as monthIdentified,\n" +
			" b.DayIdentified as dayIdentified\n" +
			" FROM biocode b, biocode_collecting_event e \n" +
			"where b.Coll_EventID = e.EventID\n" +
			"AND (b.Specimen_Num_Collector like 'BCURA%' OR b.Specimen_Num_Collector like 'CURI%')\n" +
			"AND e.yearCollected =" + year +"\n";
		//sql += queryPrefixList(prefixList);
		sql += "GROUP BY character_sanitizer(b.Specimen_Num_Collector),b.projectCode";

		occurrenceDataFile = new File(tmpDirName + File.separatorChar + projectCode +File.separatorChar + expedition + "_Specimens.txt");
		occurrenceDataFile.getParentFile().mkdirs();
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
			helpf.printHelp("exportForGeome ", options, true);
			return;
		}

		if (cl.hasOption("h")) {
			helpf.printHelp("exportForGeome ", options, true);
			return;
		}
		if (cl.hasOption("o")) {
			outputDirectory = cl.getOptionValue("o");
		}
		if (cl.hasOption("p")) {
			projectCode = cl.getOptionValue("p");
		}

		// Dump Data
		exportDropForGeome d = new exportDropForGeome(outputDirectory);


// Chris meyer sends me a 3 column list of coll_eventid_collector,project,expedition which i load into 
// indo_join.  i then call the following list of project names and expeditions for each.
d.run(d,"DROP","DROP2013",2013); //553
d.run(d,"DROP","DROP2015",2015); //728
d.run(d,"DROP","DROP2016",2016); //673
d.run(d,"DROP","DROP2017",2017); //879
	}

	// run each of the methods
	public void run(exportDropForGeome d,String projectCode, String expedition, Integer year) {
	    try {
			//Specimens are dumped in the tissue query
			//d.dumpSpecimens(projectCode, expedition, year);
			d.dumpCollectingEvents(projectCode, expedition, year);
			d.dumpTissues(projectCode, expedition, year);
	    } catch(Exception e) {
		System.err.println(e);
	    }
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
