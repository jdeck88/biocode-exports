package exports;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A Class to run specific bulk operations... mostly for a one-off basis
 */
public class miscellaneousOperations extends connector {
    /**
     * The constructor for this class takes a "subPath" designating where the output files for
     * the exports will live, e.g. "isaTab", "BOLD", "Merritt"
     *
     * @param subPath
     *
     * @throws Exception
     */
    public miscellaneousOperations(String subPath) throws Exception {
        super(subPath);
    }

    /**
     * A one-time use function to delete SIMammals Data
     *
     * @throws SQLException
     * @throws IOException
     */
    public void deleteSIMammals() throws SQLException, IOException {
        // first delete tissues
        Statement stmt = conn.createStatement();
        String sql = "";
        /*
        // DELETE FOR ALL SIMammal subsubproject  -- March, 2014, JBD
         sql =

                "SELECT\n" +
                        "\tbiocode.bnhm_id,biocode_collecting_event.eventID " +

                        "\nFROM biocode, biocode_collecting_event, biocode_tissue" +

                        // Where
                        "\nWHERE" +
                        "\n\tbiocode.ProjectCode = 'SLAB'" +
                        " &&\n\tbiocode.SubProject = 'SIBARCODE'" +
                        " &&\n\tbiocode.SubSubProject = 'SIMammal'" +
                        " &&\n\tbiocode.coll_eventID = biocode_collecting_event.eventid" +
                        " &&\n\tbiocode.bnhm_id = biocode_tissue.bnhm_id\n";
        */

        //DELETE FOR ALL CarBee-% plates   -- May 20, 2014, JBD
        sql =
                "SELECT\n" +
                        "\tbiocode.bnhm_id,biocode_collecting_event.eventID " +

                        "\nFROM biocode, biocode_collecting_event, biocode_tissue" +

                        // Where
                        "\nWHERE" +
                        "\n\tbiocode_tissue.format_name96 like 'CarBee-%'" +
                        " &&\n\tbiocode.coll_eventID = biocode_collecting_event.eventid" +
                        " &&\n\tbiocode.bnhm_id = biocode_tissue.bnhm_id\n";


        System.out.println(sql);

        delete(stmt.executeQuery(sql));
    }

    /**
     * This command just formats the SQL statement, meant to be run on server...
     * First print out the header to paste onto file
     * Second print out the rest of the data
     * TODO: filter on MBIO only
     * TODO: datacleanup/updating on GUID fields
     * TODO: Make an extract straight from this script, using headers
     *
     * @throws SQLException
     * @throws IOException
     */
    public void dumpBiocodeForZurich() throws SQLException, IOException {
        // first delete tissues

        String sql = "";
        //ArrayList result = fieldList();
        // print out the header
        System.out.println(fieldList());
        sql =
                "SELECT\n" +
                        fieldList() +
                        "\nFROM " +
                        "\n\tbiocode, biocode_collecting_event " +
                        // Where
                        "\nWHERE" +
                        " \n\tbiocode.coll_eventID = biocode_collecting_event.eventid" +
                        " &&\n\tbiocode.HoldingInstitution is not null" +
                        " &&\n\tbiocode_collecting_event.YearCollected is not null" +
                        " &&\n\tbiocode_collecting_event.MonthCollected is not null" +
                        " &&\n\tbiocode_collecting_event.DayCollected is not null" +
                        " &&\n\tbiocode_collecting_event.DecimalLongitude is not null" +
                        " &&\n\tbiocode_collecting_event.DecimalLatitude is not null" +
                        " &&\n\tbiocode.ScientificName is not null" +
                        " \nINTO OUTFILE '/tmp/biocode.dump'";

        // print out contents
        System.out.println(sql);

        //stmt.executeQuery(sql);
    }

    private void delete(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Statement stmtU = conn.createStatement();

            // Now that we have our resultSet

            String bnhm_id = rs.getString("bnhm_id");
            String eventID = rs.getString("eventID");

            // Copy into _deleted tables

            String biocodeDeleted = "INSERT INTO biocode_deleted SELECT * FROM biocode WHERE bnhm_id='" + bnhm_id + "'";
            String eventDeleted = "INSERT INTO biocode_collecting_event_deleted SELECT * FROM biocode_collecting_event WHERE eventID = " + eventID;
            String tissueDeleted = "INSERT INTO biocode_tissue_deleted SELECT * FROM biocode_tissue WHERE bnhm_id ='" + bnhm_id + "'";

            System.out.println(biocodeDeleted);
            stmtU.executeUpdate(biocodeDeleted);

            System.out.println(eventDeleted);
            stmtU.executeUpdate(eventDeleted);

            System.out.println(tissueDeleted);
            stmtU.executeUpdate(tissueDeleted);


            // Now Delete the data
            String biocodeDeleteStmt = "DELETE FROM biocode WHERE bnhm_id='" + bnhm_id + "'";
            String eventDeleteStmt = "DELETE FROM biocode_collecting_event WHERE eventID = " + eventID;
            String tissueDeleteStmt = "DELETE FROM biocode_tissue WHERE bnhm_id ='" + bnhm_id + "'";

            System.out.println(biocodeDeleteStmt);
            stmtU.executeUpdate(biocodeDeleteStmt);

            System.out.println(eventDeleteStmt);
            stmtU.executeUpdate(eventDeleteStmt);

            System.out.println(tissueDeleteStmt);
            stmtU.executeUpdate(tissueDeleteStmt);

            stmtU.close();
        }

        // then delete collecting events

        // the delete specimens


    }

    public static void main(String[] args) throws Exception {

        miscellaneousOperations o = new miscellaneousOperations(null);

        //o.deleteSIMammals();
        //o.deletePlate();
        o.dumpBiocodeForZurich();


    }

    private String fieldList() {
        String fields = "biocode.DateFirstEntered,\n" +
                "biocode.EnteredBy,\n" +
                "biocode.DateLastModified,\n" +
                "biocode.ModifiedBy,\n" +
                "biocode.ModifyReason,\n" +
                "biocode.ProjectCode,\n" +
                "biocode.OrigInstitution,\n" +
                "biocode.Specimen_Num_Collector,\n" +
                "biocode.CatalogNumberNumeric,\n" +
                "biocode.AccessionNumber,\n" +
                "biocode.VerbatimCollectingLabel,\n" +
                "biocode.VerbatimIDLabel,\n" +
                "biocode.CollectingLabelNotes,\n" +
                "biocode.specimen_ElevationMeters,\n" +
                "biocode.specimen_MinDepthMeters,\n" +
                "biocode.specimen_MaxDepthMeters,\n" +
                "biocode.ScientificName,\n" +
                "biocode.ColloquialName,\n" +
                "biocode.Kingdom,\n" +
                "biocode.Phylum,\n" +
                "biocode.Subphylum,\n" +
                "biocode.Superclass,\n" +
                "biocode.Class,\n" +
                "biocode.Subclass,\n" +
                "biocode.Infraclass,\n" +
                "biocode.Superorder,\n" +
                "biocode.Ordr,\n" +
                "biocode.Suborder,\n" +
                "biocode.Infraorder,\n" +
                "biocode.Superfamily,\n" +
                "biocode.Family,\n" +
                "biocode.Subfamily,\n" +
                "biocode.Tribe,\n" +
                "biocode.Subtribe,\n" +
                "biocode.Genus,\n" +
                "biocode.Subgenus,\n" +
                "biocode.SpecificEpithet,\n" +
                "biocode.SubspecificEpithet,\n" +
                "biocode.ScientificNameAuthor,\n" +
                "biocode.MorphoSpecies_Match,\n" +
                "biocode.MorphoSpecies_Description,\n" +
                "biocode.LowestTaxon,\n" +
                "biocode.LowestTaxonLevel,\n" +
                "biocode.IdentifiedBy,\n" +
                "biocode.IdentifiedInstitution,\n" +
                "biocode.BasisOfID,\n" +
                "biocode.YearIdentified,\n" +
                "biocode.MonthIdentified,\n" +
                "biocode.DayIdentified,\n" +
                "biocode.PreviousID,\n" +
                "biocode.TypeStatus,\n" +
                "biocode.SexCaste,\n" +
                "biocode.LifeStage,\n" +
                "biocode.Parts,\n" +
                "biocode.Weight,\n" +
                "biocode.WeightUnits,\n" +
                "biocode.Length,\n" +
                "biocode.LengthUnits,\n" +
                "biocode.PreparationType,\n" +
                "biocode.preservative,\n" +
                "biocode.fixative,\n" +
                "biocode.relaxant,\n" +
                "biocode.IndividualCount,\n" +
                "biocode.specimen_Habitat,\n" +
                "biocode.specimen_MicroHabitat,\n" +
                "biocode.Associated_Taxon,\n" +
                "biocode.Cultivated,\n" +
                "biocode.association_type,\n" +
                "biocode.color,\n" +
                "biocode.VoucherCatalogNumber,\n" +
                "biocode.Voucher_URI,\n" +
                "biocode.RelatedCatalogItem,\n" +
                "biocode.PublicAccess,\n" +
                "biocode.Notes,\n" +
                "biocode.pic,\n" +
                "biocode.bnhm_id,\n" +
                "biocode.record_source,\n" +
                "biocode.dl_notes,\n" +
                "biocode.DNASequenceNo,\n" +
                "biocode.RecheckFlag,\n" +
                "biocode.HoldingInstitution,\n" +
                "biocode.Coll_EventID,\n" +
                "biocode.Taxon_Certainty,\n" +
                "biocode.Tissue,\n" +
                "biocode.namesoup,\n" +
                "biocode.LowestTaxon_Generated,\n" +
                "biocode.parent_record,\n" +
                "biocode.child_exists,\n" +
                "biocode.batch_id,\n" +
                "biocode.SubProject,\n" +
                "biocode.SubSubProject,\n" +
                "biocode.guid,\n" +
                "biocode.taxonguid,\n" +
                "biocode_collecting_event.EventID,\n" +
                "biocode_collecting_event.OtherEventID,\n" +
                "biocode_collecting_event.ProjectCode,\n" +
                "biocode_collecting_event.HoldingInstitution,\n" +
                "biocode_collecting_event.DateFirstEntered,\n" +
                "biocode_collecting_event.EnteredBy,\n" +
                "biocode_collecting_event.DateLastModified,\n" +
                "biocode_collecting_event.ModifiedBy,\n" +
                "biocode_collecting_event.Collector_List,\n" +
                "biocode_collecting_event.YearCollected,\n" +
                "biocode_collecting_event.MonthCollected,\n" +
                "biocode_collecting_event.DayCollected,\n" +
                "biocode_collecting_event.TimeofDay,\n" +
                "biocode_collecting_event.YearCollected2,\n" +
                "biocode_collecting_event.MonthCollected2,\n" +
                "biocode_collecting_event.DayCollected2,\n" +
                "biocode_collecting_event.TimeofDay2,\n" +
                "biocode_collecting_event.ContinentOcean,\n" +
                "biocode_collecting_event.IslandGroup,\n" +
                "biocode_collecting_event.Island,\n" +
                "biocode_collecting_event.Country,\n" +
                "biocode_collecting_event.StateProvince,\n" +
                "biocode_collecting_event.County,\n" +
                "biocode_collecting_event.Locality,\n" +
                "biocode_collecting_event.Loc_Num,\n" +
                "biocode_collecting_event.DecimalLongitude,\n" +
                "biocode_collecting_event.DecimalLatitude,\n" +
                "biocode_collecting_event.DecimalLongitude2,\n" +
                "biocode_collecting_event.DecimalLatitude2,\n" +
                "biocode_collecting_event.HorizontalDatum,\n" +
                "biocode_collecting_event.MaxErrorInMeters,\n" +
                "biocode_collecting_event.MinElevationMeters,\n" +
                "biocode_collecting_event.MaxElevationMeters,\n" +
                "biocode_collecting_event.MinDepthMeters,\n" +
                "biocode_collecting_event.MaxDepthMeters,\n" +
                "biocode_collecting_event.DepthOfBottomMeters,\n" +
                "biocode_collecting_event.DepthErrorMeters,\n" +
                "biocode_collecting_event.IndividualCount,\n" +
                "biocode_collecting_event.Habitat,\n" +
                "biocode_collecting_event.MicroHabitat,\n" +
                "biocode_collecting_event.Collection_Method,\n" +
                "biocode_collecting_event.Landowner,\n" +
                "biocode_collecting_event.Permit_Info,\n" +
                "biocode_collecting_event.Remarks,\n" +
                "biocode_collecting_event.OtherEventInst,\n" +
                "biocode_collecting_event.OtherEventID2,\n" +
                "biocode_collecting_event.TaxTeam,\n" +
                "biocode_collecting_event.VerbatimLongitude,\n" +
                "biocode_collecting_event.VerbatimLatitude,\n" +
                "biocode_collecting_event.VerbatimLongitude2,\n" +
                "biocode_collecting_event.VerbatimLatitude2,\n" +
                "biocode_collecting_event.Disposition,\n" +
                "biocode_collecting_event.TaxonNotes,\n" +
                "biocode_collecting_event.Coll_EventID_collector,\n" +
                "biocode_collecting_event.pic,\n" +
                "biocode_collecting_event.batch_id,\n" +
                "biocode_collecting_event.guid";

        //ArrayList<String> result = new ArrayList<String>();
        //result.add(fields);
        //result.add(fields.replace(",\n", "\t"));
        return fields;

    }

}
