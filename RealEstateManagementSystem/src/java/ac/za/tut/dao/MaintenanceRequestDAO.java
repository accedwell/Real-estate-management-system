package ac.za.tut.dao;

import ac.za.tut.model.MaintenanceRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceRequestDAO {

    public void addRequest(MaintenanceRequest req) throws SQLException {
        String sql = "INSERT INTO MaintenanceRequests (tenantID, propertyID, description, priority, status, cost, resolutionNotes) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, req.getTenantID());
            ps.setInt(2, req.getPropertyID());
            ps.setString(3, req.getDescription());
            ps.setString(4, req.getPriority());
            ps.setString(5, req.getStatus());
            ps.setDouble(6, req.getCost());
            ps.setString(7, req.getResolutionNotes());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int requestID, String status, String resolutionNotes) throws SQLException {
        String sql = "UPDATE MaintenanceRequests SET status=?, resolutionNotes=? WHERE requestID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, resolutionNotes);
            ps.setInt(3, requestID);
            ps.executeUpdate();
        }
    }

    public void removeRequest(int requestID) throws SQLException {
        String sql = "DELETE FROM MaintenanceRequests WHERE requestID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestID);
            ps.executeUpdate();
        }
    }

    public MaintenanceRequest getRequestById(int requestID) throws SQLException {
        String sql = "SELECT * FROM MaintenanceRequests WHERE requestID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new MaintenanceRequest(
                    rs.getInt("requestID"),
                    rs.getInt("tenantID"),
                    rs.getInt("propertyID"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getDouble("cost"),
                    rs.getString("resolutionNotes")
                );
            }
        }
        return null;
    }
public void deleteRequest(int requestID) throws SQLException {
        String sql = "DELETE FROM MaintenanceRequests WHERE requestID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestID);
            ps.executeUpdate();
        }
    }
    public List<MaintenanceRequest> getAllRequests() throws SQLException {
        List<MaintenanceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM MaintenanceRequests";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(new MaintenanceRequest(
                    rs.getInt("requestID"),
                    rs.getInt("tenantID"),
                    rs.getInt("propertyID"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getDouble("cost"),
                    rs.getString("resolutionNotes")
                ));
            }
        }
        return requests;
    }
    public void updateRequest(MaintenanceRequest req) throws SQLException {
    String sql = "UPDATE MaintenanceRequests SET tenantID=?, propertyID=?, description=?, priority=?, status=?, cost=?, resolutionNotes=? WHERE requestID=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, req.getTenantID());
        ps.setInt(2, req.getPropertyID());
        ps.setString(3, req.getDescription());
        ps.setString(4, req.getPriority());
        ps.setString(5, req.getStatus());
        ps.setDouble(6, req.getCost());
        ps.setString(7, req.getResolutionNotes());
        ps.setInt(8, req.getRequestID());
        ps.executeUpdate();
    }
}


    /**
     * Requests with tenant name and property address for display.
     * Pass a userID to show only the requests logged on that user's leases.
     */
    public List<Map<String, String>> getRequestDetails(Integer userID) throws SQLException {
        List<Map<String, String>> rows = new ArrayList<>();
        String sql = "SELECT m.*, u.name AS tenantName, p.address "
                + "FROM MaintenanceRequests m "
                + "JOIN Tenants t ON m.tenantID = t.tenantID "
                + "JOIN Users u ON t.userID = u.userID "
                + "JOIN Properties p ON m.propertyID = p.propertyID"
                + (userID != null ? " WHERE t.userID = ?" : "")
                + " ORDER BY CASE m.status WHEN 'Pending' THEN 0 WHEN 'InProgress' THEN 1 ELSE 2 END, "
                + "CASE m.priority WHEN 'High' THEN 0 WHEN 'Medium' THEN 1 ELSE 2 END, m.requestID DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userID != null) {
                ps.setInt(1, userID);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("requestID", String.valueOf(rs.getInt("requestID")));
                    row.put("tenantID", String.valueOf(rs.getInt("tenantID")));
                    row.put("propertyID", String.valueOf(rs.getInt("propertyID")));
                    row.put("tenantName", rs.getString("tenantName"));
                    row.put("address", rs.getString("address"));
                    row.put("description", rs.getString("description"));
                    row.put("priority", rs.getString("priority"));
                    row.put("status", rs.getString("status"));
                    row.put("cost", String.format(java.util.Locale.ROOT, "%.2f", rs.getDouble("cost")));
                    row.put("resolutionNotes", rs.getString("resolutionNotes"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

}
