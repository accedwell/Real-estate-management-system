package ac.za.tut.dao;


import ac.za.tut.entities.Application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ApplicationDAO {

    public void addApplication(Application app) throws SQLException {
        String sql = "INSERT INTO Applications (tenantID, propertyID, date, status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getTenantID());
            ps.setInt(2, app.getPropertyID());
            ps.setDate(3, app.getDate());
            ps.setString(4, app.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int applicationID, String status) throws SQLException {
        String sql = "UPDATE Applications SET status=? WHERE applicationID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationID);
            ps.executeUpdate();
        }
    }

    public void removeApplication(int applicationID) throws SQLException {
        String sql = "DELETE FROM Applications WHERE applicationID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationID);
            ps.executeUpdate();
        }
    }

    public Application getApplicationById(int applicationID) throws SQLException {
        String sql = "SELECT * FROM Applications WHERE applicationID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Application(
                    rs.getInt("applicationID"),
                    rs.getInt("tenantID"),
                    rs.getInt("propertyID"),
                    rs.getDate("date"),
                    rs.getString("status")
                );
            }
        }
        return null;
    }

    public List<Application> getAllApplications() throws SQLException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT * FROM Applications";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                apps.add(new Application(
                    rs.getInt("applicationID"),
                    rs.getInt("tenantID"),
                    rs.getInt("propertyID"),
                    rs.getDate("date"),
                    rs.getString("status")
                ));
            }
        }
        return apps;
    }
    public void updateApplication(Application app) throws SQLException {
    String sql = "UPDATE Applications SET tenantID=?, propertyID=?, date=?, status=? WHERE applicationID=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, app.getTenantID());
        ps.setInt(2, app.getPropertyID());
        ps.setDate(3, app.getDate());
        ps.setString(4, app.getStatus());
        ps.setInt(5, app.getApplicationID());
        ps.executeUpdate();
    }
}
// Add this inside your ApplicationDAO class
    public List<Application> getApplicationsByTenant(int tenantID) throws SQLException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT * FROM Applications WHERE tenantID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    apps.add(new Application(
                        rs.getInt("applicationID"),
                        rs.getInt("tenantID"),
                        rs.getInt("propertyID"),
                        rs.getDate("date"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return apps;
    }

    /**
     * Applications with applicant name and property address.
     * Pass a tenant userID to show only that tenant's applications.
     */
    public List<Map<String, String>> getApplicationDetails(Integer tenantUserID) throws SQLException {
        List<Map<String, String>> rows = new ArrayList<>();
        String sql = "SELECT a.*, u.name AS applicantName, p.address, p.type "
                + "FROM Applications a JOIN Users u ON a.tenantID = u.userID "
                + "JOIN Properties p ON a.propertyID = p.propertyID"
                + (tenantUserID != null ? " WHERE a.tenantID = ?" : "")
                + " ORDER BY a.date DESC, a.applicationID DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tenantUserID != null) {
                ps.setInt(1, tenantUserID);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("applicationID", String.valueOf(rs.getInt("applicationID")));
                    row.put("tenantID", String.valueOf(rs.getInt("tenantID")));
                    row.put("propertyID", String.valueOf(rs.getInt("propertyID")));
                    row.put("applicantName", rs.getString("applicantName"));
                    row.put("address", rs.getString("address"));
                    row.put("type", rs.getString("type"));
                    row.put("date", String.valueOf(rs.getDate("date")));
                    row.put("status", rs.getString("status"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

}
