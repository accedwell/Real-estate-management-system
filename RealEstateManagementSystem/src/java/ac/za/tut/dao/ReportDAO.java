package ac.za.tut.dao;

import ac.za.tut.entities.Report;
import java.sql.*;
import java.util.*;

public class ReportDAO {

    public void addReport(Report report) throws SQLException {
        String sql = "INSERT INTO Reports (type, generatedBy, date, filePath) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, report.getType());
            ps.setInt(2, report.getGeneratedBy());   // FIXED
            ps.setDate(3, report.getDate());
            ps.setString(4, report.getFilePath());
            ps.executeUpdate();
        }
    }

    public void updateReport(Report report) throws SQLException {
        String sql = "UPDATE Reports SET type=?, generatedBy=?, date=?, filePath=? WHERE reportID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, report.getType());
            ps.setInt(2, report.getGeneratedBy());   // FIXED
            ps.setDate(3, report.getDate());
            ps.setString(4, report.getFilePath());
            ps.setInt(5, report.getReportID());
            ps.executeUpdate();
        }
    }

    public void removeReport(int reportID) throws SQLException {
        String sql = "DELETE FROM Reports WHERE reportID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportID);
            ps.executeUpdate();
        }
    }

    public Report getReportById(int reportID) throws SQLException {
        String sql = "SELECT * FROM Reports WHERE reportID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Report(
                    rs.getInt("reportID"),
                    rs.getString("type"),
                    rs.getInt("generatedBy"),   // FIXED
                    rs.getDate("date"),
                    rs.getString("filePath")
                );
            }
        }
        return null;
    }

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reports.add(new Report(
                    rs.getInt("reportID"),
                    rs.getString("type"),
                    rs.getInt("generatedBy"),   // FIXED
                    rs.getDate("date"),         // FIXED: Correct ResultSet getter method call
                    rs.getString("filePath")
                ));
            }
        }
        return reports;
    }

    // =========================================================================
    // REAL-TIME JOINED DATABASE QUERIES FOR RUBRIC EVALUATION
    // =========================================================================

    /**
     * Rubric Component 1: Management Summary Metrics
     * Dynamically pulls total counts across core entities for executive evaluation.
     */
    public Map<String, Integer> getSummaryMetrics() throws SQLException {
        Map<String, Integer> metrics = new HashMap<>();
        try (Connection conn = DBConnection.getConnection(); 
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM Properties");
            if (rs1.next()) metrics.put("totalProperties", rs1.getInt(1));
            
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM Tenants");
            if (rs2.next()) metrics.put("activeLeases", rs2.getInt(1));
            
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM MaintenanceRequests WHERE status != 'Resolved'");
            if (rs3.next()) metrics.put("openMaintenance", rs3.getInt(1));
            
            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM Applications WHERE status = 'Pending'");
            if (rs4.next()) metrics.put("pendingApps", rs4.getInt(1));
        }
        return metrics;
    }

    /**
     * Rubric Component 2: Multi-Table Active Leases Report (UPDATED WITH DATE FILTERS)
     * Joins Tenants, Users, and Properties to provide full management utility.
     */
    public List<Map<String, String>> getTenantLeaseReport(String startDate, String endDate) throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();
        
        // Base query with a WHERE 1=1 clause to easily append filters
        String sql = "SELECT u.name AS tenantName, p.address, t.leaseStart, t.leaseEnd " +
                     "FROM Tenants t " +
                     "JOIN Users u ON t.userID = u.userID " +
                     "JOIN Properties p ON t.propertyID = p.propertyID " +
                     "WHERE 1=1";
                     
        // Check if user provided date filters
        boolean hasDateFilter = (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty());
        
        if (hasDateFilter) {
            sql += " AND t.leaseStart >= ? AND t.leaseEnd <= ?";
        }

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Bind date parameters securely if they exist
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
                    list.add(row);
                }
            }
        }
        return list;
    }

    /**
     * Rubric Component 3: Multi-Table Applications Tracking Report (UPDATED WITH STATUS FILTER)
     * Joins Applications, Users, and Properties to audit prospective workflows.
     */
    public List<Map<String, String>> getApplicationReport(String statusFilter) throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();
        
        // Base query with a WHERE 1=1 clause to easily append filters
        String sql = "SELECT u.name AS applicantName, p.address, a.date, a.status " +
                     "FROM Applications a " +
                     "JOIN Users u ON a.tenantID = u.userID " +
                     "JOIN Properties p ON a.propertyID = p.propertyID " +
                     "WHERE 1=1";
                     
        // Check if a specific status filter was provided (ignoring "ALL")
        boolean hasStatusFilter = (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter));
        
        if (hasStatusFilter) {
            sql += " AND a.status = ?";
        }
                     
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Bind the status parameter securely if it exists
            if (hasStatusFilter) {
                ps.setString(1, statusFilter);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("applicantName", rs.getString("applicantName"));
                    row.put("address", rs.getString("address"));
                    row.put("date", rs.getString("date"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    /**
     * Rubric Component 4: Fully Filtered Multi-Table Maintenance Report
     * Joins MaintenanceRequests, Properties, Tenants, and Users.
     * Evaluates dynamic status filters, priority flags, and logged-in user constraints.
     */
    public List<Map<String, String>> getMaintenanceReport(int userID, String role, String status, String priority) throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();
        if (status == null || status.trim().isEmpty() || "null".equals(status)) { status = "ALL"; }
        if (priority == null || priority.trim().isEmpty() || "null".equals(priority)) { priority = "ALL"; }

        StringBuilder sql = new StringBuilder(
            "SELECT p.address, u.name AS tenantName, m.description, m.priority, m.cost, m.status " +
            "FROM MaintenanceRequests m " +
            "JOIN Properties p ON m.propertyID = p.propertyID " +
            "JOIN Tenants t ON m.tenantID = t.tenantID " +
            "JOIN Users u ON t.userID = u.userID " +
            "WHERE 1=1"
        );

        if (!"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND m.status = ?");
        }

        if (!"ALL".equalsIgnoreCase(priority)) {
            sql.append(" AND m.priority = ?");
        }

        if ("Owner".equalsIgnoreCase(role)) {
            sql.append(" AND p.ownerID = ?");
        }

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (!"ALL".equalsIgnoreCase(status)) {
                ps.setString(paramIndex++, status);
            }
            if (!"ALL".equalsIgnoreCase(priority)) {
                ps.setString(paramIndex++, priority);
            }
            if ("Owner".equalsIgnoreCase(role)) {
                ps.setInt(paramIndex, userID);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("address", rs.getString("address"));
                    row.put("tenantName", rs.getString("tenantName"));
                    row.put("description", rs.getString("description"));
                    row.put("priority", rs.getString("priority"));
                    row.put("cost", rs.getString("cost"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
        }
        return list;
    }
}