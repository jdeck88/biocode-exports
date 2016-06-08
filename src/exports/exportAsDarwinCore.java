package exports;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Export Biocode Project to Darwin Core Archive
 */
public class exportAsDarwinCore extends connector {

    File uniqueNameFile;
    String tmpDirName;


    public exportAsDarwinCore() throws Exception {
        super("names");

        tmpDirName = this.processDirectory.getAbsoluteFile().toString();
    }

    /**
     * Dump Names
     *
     * @throws java.sql.SQLException
     */
    public String dumpProjectCode(String projectCode) throws SQLException, IOException {
        //projectCode = "MBIOINDO";
        Statement stmt = conn.createStatement();
        String sql =
                "select \n" +
                        "  b.guid as occurrenceID,\n" +
                        "  'PreservedSpecimen' as basisOfRecord,\n" +
                        "  b.HoldingInstitution as institutionCode,\n" +
                        "  b.Specimen_Num_Collector as recordNumber,\n" +
                        "  b.ScientificName as scientificName,\n" +
                        "  b.Kingdom as kingdom,\n" +
                        "  b.Phylum as phylum,\n" +
                        "  b.Class as class,  \n" +
                        "  b.Ordr as 'order',\n" +
                        "  b.Family as family,\n" +
                        "  b.Genus as genus,\n" +
                        "  b.Subgenus as subgenus,\n" +
                        "  b.SpecificEpithet as specificEpithet,\n" +
                        "  b.SubspecificEpithet as infraspecificEpithet,\n" +
                        "  b.IdentifiedBy as identifiedBy,\n" +
                        "  b.BasisOfID as identificationRemarks,\n" +
                        "  concat(b.YearIdentified,\"-\",b.MonthIdentified, \"-\",b.DayIdentified) as dateIdentified,\n" +
                        "  b.TypeStatus as typeStatus,\n" +
                        "  b.SexCaste as sex,\n" +
                        "  b.LifeStage as lifeStage,\n" +
                        "  e.guid as eventID,\n" +
                        "  e.Collector_List as recordedBy,\n" +
                        "  e.YearCollected as year,\n" +
                        "  e.MonthCollected as month,\n" +
                        "  e.DayCollected as day,\n" +
                        "  e.TimeofDay as eventTime, \n" +
                        "  e.ContinentOcean as continent,\n" +
                        "  e.IslandGroup as islandGroup,\n" +
                        "  e.Island as island,\n" +
                        "  e.Country as country,\n" +
                        "  e.StateProvince as stateProvince,\n" +
                        "  e.County as county,\n" +
                        "  e.Locality as locality,\n" +
                        "  e.DecimalLongitude as decimalLatitude,\n" +
                        "  e.DecimalLatitude as decimalLongitude,  \n" +
                        "  e.HorizontalDatum as geodeticDatum,\n" +
                        "  cast(e.MaxErrorInMeters as unsigned integer) as coordinateUncertaintyInMeters,\n" +
                        "  cast(e.MinElevationMeters as unsigned integer) as minimumElevationInMeters,\n" +
                        "  cast(e.MaxElevationMeters as unsigned integer) as maximumElevationInMeters,\n" +
                        "  cast(e.MinDepthMeters as unsigned integer) as minimumDepthInMeters,\n" +
                        "  cast(e.MaxDepthMeters as unsigned integer) as maximumDepthInMeters,  \n" +
                        "  cast(e.IndividualCount as unsigned integer) as individualCount,\n" +
                        "  e.Habitat as habitat,\n" +
                        "  e.Collection_Method as samplingProtocol,\n" +
                        "  e.Remarks as eventRemarks,\n" +
                        "  e.VerbatimLongitude as verbatimLongitude,\n" +
                        "  e.VerbatimLatitude as verbatimLatitude,\n" +
                        "  e.Coll_EventID_collector as fieldNumber\n" +
                        "FROM biocode b, biocode_collecting_event e\n" +
                        "where b.Coll_EventID = e.EventID\n" +
                        "AND b.projectCode = '" + projectCode + "';\n";


        String outputDir = tmpDirName + File.separatorChar + projectCode + ".txt";

        System.out.println("Writing output to " + outputDir);

        uniqueNameFile = new File(tmpDirName + File.separatorChar + projectCode + ".txt");

        return writeResultSet(stmt.executeQuery(sql), uniqueNameFile);
    }

    public static void main(String[] args) throws Exception {
        exportAsDarwinCore d = new exportAsDarwinCore();
        d.dumpProjectCode("MBIO");
        //d.dumpProjectCode("INDO");
    }
}