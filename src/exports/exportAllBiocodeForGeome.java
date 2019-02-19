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
 * Export for All Biocode projects into Geome
 */
public class exportAllBiocodeForGeome extends connector {
	static final int BUFFER = 2048;

	static File occurrenceDataFile;
	String tmpDirName;

	public exportAllBiocodeForGeome (String processDirectory) throws Exception {
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
	public String dumpCollectingEvents(String projectCode, String expedition) throws SQLException, IOException {
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
			"  e.HorizontalDatum  as horizontalDatum,\n" +
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
			"AND b.geome_project = '" + projectCode + "'\n" +
			"AND b.geome_expedition = '" + expedition + "'\n"; 
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

	public String dumpTissues(String projectCode,String expedition) throws SQLException, IOException {
		Statement stmt = conn.createStatement();
		String sql =
			"select \n" +
			" character_sanitizer(concat(b.bnhm_id,'.',tissue_num)) as tissueID,\n" +
			" character_sanitizer(b.Specimen_Num_Collector) as materialSampleID,\n" +
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
			"FROM biocode b, biocode_collecting_event e, biocode_tissue t \n" +
			"where b.Coll_EventID = e.EventID\n" +
			"AND b.bnhm_id=t.bnhm_id\n" + 
			"AND b.geome_project = '" + projectCode + "'\n" +
			"AND b.geome_expedition = '" + expedition + "'\n"; 
		//sql += queryPrefixList(prefixList);

		occurrenceDataFile = new File(tmpDirName + File.separatorChar + projectCode + File.separatorChar + expedition + "_Tissues.txt");
		occurrenceDataFile.getParentFile().mkdirs();
		return writeResultSet(stmt.executeQuery(sql), occurrenceDataFile);
	}

	public String dumpSpecimens(String projectCode, String expedition) throws SQLException, IOException {
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
			"FROM biocode b, biocode_collecting_event e \n" +
			"WHERE b.Coll_EventID = e.EventID \n" +
			"AND b.geome_project = '" + projectCode + "'\n" +
			"AND b.geome_expedition = '" + expedition + "'\n"; 
		//sql += queryPrefixList(prefixList);
		sql += "GROUP BY character_sanitizer(b.Specimen_Num_Collector),b.geome_project";

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
		String expeditionCode = "";
		String outputDirectory = null;

		// Define our commandline options
		Options options = new Options();
		options.addOption("h", "help", false, "print this help message and exit");
		options.addOption("o", "outputDirectory", true, "Output Directory");
		options.addOption("p", "projectCode", true, "Project Code");
		options.addOption("e", "expeditionCode", true, "Expedition Code");

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
		if (cl.getOptions().length < 3) {
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
		if (cl.hasOption("e")) {
			expeditionCode = cl.getOptionValue("e");
		}

		// Dump Data
		exportAllBiocodeForGeome d = new exportAllBiocodeForGeome(outputDirectory);
		d.run(d,projectCode,expeditionCode);
	}

	// run each of the methods
	public void run(exportAllBiocodeForGeome d,String projectCode, String expeditionCode) {
	    try {
			d.dumpSpecimens(projectCode, expeditionCode);
			d.dumpCollectingEvents(projectCode, expeditionCode);
			d.dumpTissues(projectCode, expeditionCode);
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
