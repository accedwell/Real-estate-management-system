package ac.za.tut.dao;

import ac.za.tut.entities.Property;
import ac.za.tut.entities.User;
import java.sql.*;
import java.util.*;

public class PropertyDAO {

    /** Base query that also returns the owner's name from the Users table. */
    private static final String SELECT_WITH_OWNER =
            "SELECT p.*, u.name AS ownerName FROM Properties p LEFT JOIN Users u ON p.ownerID = u.userID";

    /** Properties belonging to one owner. */
    public List<Property> getPropertiesByOwner(int ownerID) throws SQLException {
        List<Property> list = new ArrayList<>();
        String sql = SELECT_WITH_OWNER + " WHERE p.ownerID = ? ORDER BY p.propertyID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPropertyFromResultSet(rs));
                }
            }
        }
        return list;
    }

    /** Changes only the listing status (Available / Rented / Sold). */
    public void updateStatus(int propertyID, String status) throws SQLException {
        String sql = "UPDATE Properties SET status=? WHERE propertyID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, propertyID);
            ps.executeUpdate();
        }
    }


    /** 
     * HELPER: extractPropertyFromResultSet
     * Reduces code duplication by mapping a ResultSet row to a Property object.
     */
    private Property extractPropertyFromResultSet(ResultSet rs) throws SQLException {
        return new Property(
            rs.getInt("propertyID"),
            rs.getInt("ownerID"),
            rs.getString("ownerName"),
            rs.getString("address"),
            rs.getString("type"),
            rs.getInt("size"),
            rs.getInt("rooms"),
            rs.getString("status")
        );
    }

    /** 
     * CREATE: addProperty
     * Inserts a new property listing into the database.
     */
    public void addProperty(Property property) throws SQLException {
        String sql = "INSERT INTO Properties (ownerID, address, type, size, rooms, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, property.getOwnerID());
            ps.setString(2, property.getAddress());
            ps.setString(3, property.getType());
            ps.setInt(4, property.getSize());
            ps.setInt(5, property.getRooms());
            ps.setString(6, property.getStatus());
            ps.executeUpdate();
        }
    }

    /** 
     * READ: getAllProperties
     * Fetches every property (Standard view for Admin/Agents).
     */
    public List<Property> getAllProperties() throws SQLException {
        List<Property> properties = new ArrayList<>();
        String sql = SELECT_WITH_OWNER + " ORDER BY p.propertyID";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                properties.add(extractPropertyFromResultSet(rs));
            }
        }
        return properties;
    }

    /** 
     * READ: getPropertyById
     */
    public Property getPropertyById(int id) throws SQLException {
        String sql = SELECT_WITH_OWNER + " WHERE p.propertyID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPropertyFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /** 
     * READ: getOwnedProperties
     * Fetches properties where the user is the CREATOR/OWNER.
     * (Fixes the issue for User 7).
     */
public List<User> getAllOwners() throws SQLException {
    List<User> owners = new ArrayList<>();
    // This query gets unique users who have the role 'Owner'
    String sql = "SELECT DISTINCT u.userID, u.name, u.email, u.phone, u.role " +
                 "FROM Users u " +
                 "WHERE u.role = 'Owner'";
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            owners.add(new User(
                rs.getInt("userID"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("phone"),
                null // Don't fetch passwords for the list
            ));
        }
    }
    return owners;
}
    /** 
     * READ: getRentedProperties
     * Fetches properties currently linked to the user via the Tenants table.
     */
    public List<Property> getRentedProperties(int userID) throws SQLException {
        List<Property> list = new ArrayList<>();
        String sql = SELECT_WITH_OWNER +
                     " JOIN Tenants t ON p.propertyID = t.propertyID " +
                     "WHERE t.userID = ? ORDER BY p.propertyID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPropertyFromResultSet(rs));
                }
            }
        }
        return list;
    }

    /** 
     * READ: getPurchasedProperties
     * Fetches properties where the user is the BUYER in a completed 'Sale'.
     */
public List<Property> getPurchasedProperties() throws SQLException {
    List<Property> properties = new ArrayList<>();
    // REMOVED the join with Transactions
    String sql = SELECT_WITH_OWNER + " WHERE p.status = 'Sold'";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            properties.add(extractPropertyFromResultSet(rs));
        }
    }
    return properties;
}

    /** 
     * UPDATE: updateProperty
     */
    public void updateProperty(Property property) throws SQLException {
        String sql = "UPDATE Properties SET ownerID=?, address=?, type=?, size=?, rooms=?, status=? WHERE propertyID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, property.getOwnerID());
            ps.setString(2, property.getAddress());
            ps.setString(3, property.getType());
            ps.setInt(4, property.getSize());
            ps.setInt(5, property.getRooms());
            ps.setString(6, property.getStatus());
            ps.setInt(7, property.getPropertyID());
            ps.executeUpdate();
        }
    }
    public void deleteProperty(int id) throws SQLException {
    String sql = "DELETE FROM Properties WHERE propertyID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
    
    public List<Map<String, String>> getTenantLeaseReport(String startDate, String endDate) {
        List<Map<String, String>> leaseData = new ArrayList<>();
        
        // Base SQL Query (Update the table/column names if yours are slightly different)
        String sql = "SELECT p.address, u.name AS tenantName, t.leaseStart, t.leaseEnd " +
                     "FROM Tenants t " +
                     "JOIN Properties p ON t.propertyID = p.propertyID " +
                     "JOIN Users u ON t.userID = u.userID " +
                     "WHERE 1=1";
        
        // Add the date filter dynamically if the user provided dates
        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty());
        if (hasDateFilter) {
            sql += " AND t.leaseStart >= ? AND t.leaseEnd <= ?";
        }

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Bind the dates to the query safely if they exist
            if (hasDateFilter) {
                ps.setDate(1, java.sql.Date.valueOf(startDate));
                ps.setDate(2, java.sql.Date.valueOf(endDate));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("tenantName", rs.getString("tenantName"));
                    row.put("address", rs.getString("address"));
                    row.put("leaseStart", rs.getString("leaseStart"));
                    row.put("leaseEnd", rs.getString("leaseEnd"));
                    leaseData.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return leaseData;
    }
    
}