package updates;

import exports.connector;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * universalUpdater meant to be run at a regular interval to make sure derived fields are updated
 * E.g. updating all the GUID fields in the Biocode Database
 */
public class universalUpdater extends connector {
      public universalUpdater(String subPath) throws Exception {
        super(subPath);
    }

    /**
     * Update the Biocode GUID field
     * @throws SQLException
     */
    public void updateBiocodeGuid () throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "update biocode set guid = concat('http://n2t.net/ark:/21547/R2',bnhm_id)";
        System.out.println(sql);
        stmt.executeUpdate(sql);
        stmt.close();
    }

    /**
     * Update the Biocode_collecting_event GUID field
     * @throws SQLException
     */
    public void updateBiocodeCollectingEventGuid () throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "update biocode_collecting_event set guid = concat('http://n2t.net/ark:/21547/S2',eventid)";
        System.out.println(sql);
        stmt.executeUpdate(sql);
        stmt.close();
    }

    /**
     * Update the Biocode_tissue GUID field
     * @throws SQLException
     */
    public void updateBiocodeTissueGuid () throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "update biocode_tissue set guid = concat('http://n2t.net/ark:/21547/Q2',bnhm_id,'.',tissue_num)";
        System.out.println(sql);
        stmt.executeUpdate(sql);
        stmt.close();
    }

    //update biocode set taxonguid = concat('ark:/21547/T2',if(scientificname = "","",concat("_",hex(scientificname))));

        /**
     * Update the Biocode_tissue GUID field
     * @throws SQLException
     */
    public void updateBiocodeTaxonGuid () throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "update biocode set taxonguid = concat('http://n2t.net/ark:/21547/T2',if(scientificname = \"\",\"\",hex(scientificname)))";
        System.out.println(sql);
        stmt.executeUpdate(sql);
        stmt.close();
    }
    /**
     * Update GUID fields to be run on a periodic basis
     */
    public static void main(String[] args) throws Exception {
        universalUpdater updater = new universalUpdater(null);

        //updater.updateBiocodeGuid();
        //updater.updateBiocodeCollectingEventGuid();
        //updater.updateBiocodeTissueGuid();
        updater.updateBiocodeTaxonGuid();

    }


}

