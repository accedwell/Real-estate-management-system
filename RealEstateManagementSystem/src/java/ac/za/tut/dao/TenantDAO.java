package ac.za.tut.dao;

import ac.za.tut.entities.Tenant;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for managing Tenant records in the PostgreSQL Database.
 */
public class TenantDAO {

    /**
     * Inserts a new tenant lease record into the database.
     */
    public void addTenant(Tenant tenant) throws SQLException {
        String sql = "INSERT INTO Tenants (userID, propertyID, leaseStart, leaseEnd) VALUES (?,?,?,?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tenant.getUserID());
            ps.setInt(2, tenant.getPropertyID());
            ps.setDate(3, tenant.getLeaseStart());
            ps.setDate(4, tenant.getLeaseEnd());
            
            ps.executeUpdate();
            System.out.println("SUCCESS: Tenant added to database.");
            
        } catch (SQLException e) {
            System.err.println("DB ERROR in addTenant: " + e.getMessage());
            throw e; // Re-throw to be caught by the Servlet for error messages
        }
    }

    /**
     * Updates the end date of an existing lease.
     */
    public void updateLease(int tenantID, Date leaseEnd) throws SQLException {
        String sql = "UPDATE Tenants SET leaseEnd=? WHERE tenantID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, leaseEnd);
            ps.setInt(2, tenantID);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB ERROR in updateLease: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Removes a tenant from the database.
     */
    public void removeTenant(int tenantID) throws SQLException {
        String sql = "DELETE FROM Tenants WHERE tenantID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tenantID);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("DB ERROR in removeTenant: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a specific tenant by their unique tenantID.
     */
    public Tenant getTenantById(int tenantID) throws SQLException {
        String sql = "SELECT * FROM Tenants WHERE tenantID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tenantID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tenant(
                        rs.getInt("tenantID"),
                        rs.getInt("userID"),
                        rs.getInt("propertyID"),
                        rs.getDate("leaseStart"),
                        rs.getDate("leaseEnd")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR in getTenantById: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Updates all fields for an existing tenant record.
     */
    public void updateTenant(Tenant tenant) throws SQLException {
        String sql = "UPDATE Tenants SET userID=?, propertyID=?, leaseStart=?, leaseEnd=? WHERE tenantID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tenant.getUserID());
            ps.setInt(2, tenant.getPropertyID());
            ps.setDate(3, tenant.getLeaseStart());
            ps.setDate(4, tenant.getLeaseEnd());
            ps.setInt(5, tenant.getTenantID());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("DB ERROR in updateTenant: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a list of all tenants in the system.
     */
    public List<Tenant> getAllTenants() throws SQLException {
        List<Tenant> tenants = new ArrayList<>();
        String sql = "SELECT * FROM Tenants";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tenants.add(new Tenant(
                    rs.getInt("tenantID"),
                    rs.getInt("userID"),
                    rs.getInt("propertyID"),
                    rs.getDate("leaseStart"),
                    rs.getDate("leaseEnd")
                ));
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR in getAllTenants: " + e.getMessage());
            throw e;
        }
        return tenants;
    }

    /**
     * Leases with tenant name and property address, newest first.
     * Pass a userID to restrict to one tenant user, or null for everyone.
     */
    public List<Map<String, String>> getLeaseDetails(Integer userID) throws SQLException {
        List<Map<String, String>> rows = new ArrayList<>();
        String sql = "SELECT t.tenantID, t.userID, t.propertyID, t.leaseStart, t.leaseEnd, "
                + "u.name AS tenantName, u.email, p.address "
                + "FROM Tenants t JOIN Users u ON t.userID = u.userID "
                + "JOIN Properties p ON t.propertyID = p.propertyID"
                + (userID != null ? " WHERE t.userID = ?" : "")
                + " ORDER BY t.tenantID DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userID != null) {
                ps.setInt(1, userID);
            }
            try (ResultSet rs = ps.executeQuery()) {
                Date today = new Date(System.currentTimeMillis());
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("tenantID", String.valueOf(rs.getInt("tenantID")));
                    row.put("userID", String.valueOf(rs.getInt("userID")));
                    row.put("propertyID", String.valueOf(rs.getInt("propertyID")));
                    row.put("tenantName", rs.getString("tenantName"));
                    row.put("email", rs.getString("email"));
                    row.put("address", rs.getString("address"));
                    row.put("leaseStart", String.valueOf(rs.getDate("leaseStart")));
                    Date end = rs.getDate("leaseEnd");
                    row.put("leaseEnd", end == null ? "" : end.toString());
                    row.put("status", (end == null || !end.before(today)) ? "Active" : "Ended");
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    /**
     * Records a tenant departure: closes their open lease(s) on the property
     * with today's date. Returns the number of leases closed.
     */
    public int endLease(int userID, int propertyID) throws SQLException {
        String sql = "UPDATE Tenants SET leaseEnd = CURRENT_DATE "
                + "WHERE userID = ? AND propertyID = ? AND (leaseEnd IS NULL OR leaseEnd > CURRENT_DATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, propertyID);
            return ps.executeUpdate();
        }
    }

}
