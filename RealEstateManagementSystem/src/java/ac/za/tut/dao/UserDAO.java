package ac.za.tut.dao;

import ac.za.tut.entities.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE LOWER(email)=LOWER(?) AND passwordHash=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passwordHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            ps.setString(1, email == null ? "" : email.trim());
            ps.setBytes(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("userID"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getBytes("passwordHash")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void register(User user) throws SQLException {
        addUser(user); // Reusing the addUser method to avoid duplicated code
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, role, email, phone, passwordHash) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setBytes(5, user.getPasswordHash());
            ps.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY userID";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("userID"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getBytes("passwordHash")
                ));
            }
        }
        return users;
    }

    // ====================================================================
    // NEW METHODS ADDED BELOW FOR DELETE AND EDIT FUNCTIONALITY
    // ====================================================================

    /**
     * Deletes a user by ID. 
     * Throws an SQLException if the user is linked to properties, applications, etc.
     */
    public void removeUser(int userID) throws SQLException {
        String sql = "DELETE FROM Users WHERE userID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.executeUpdate();
        }
    }

    /**
     * Fetches a single user by their ID (needed for the Edit page).
     */
    public User getUserById(int userID) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("userID"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getBytes("passwordHash")
                );
            }
        }
        return null;
    }

    /**
     * Updates an existing user's details.
     */
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET name=?, role=?, email=?, phone=? WHERE userID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getUserID());
            ps.executeUpdate();
        }
    }
    public List<User> getUsersByRole(String role) throws SQLException {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM Users WHERE role=? ORDER BY name";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            users.add(new User(
                rs.getInt("userID"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getBytes("passwordHash")
            ));
        }
    }
    return users;
}


    /** True if an account already uses this email (case-insensitive). */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Sets a new password (stored as SHA-256, matching the login check). */
    public void updatePassword(int userID, byte[] passwordHash) throws SQLException {
        String sql = "UPDATE Users SET passwordHash=? WHERE userID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, passwordHash);
            ps.setInt(2, userID);
            ps.executeUpdate();
        }
    }

    /** Hashes a raw password the same way the login check does. */
    public static byte[] hash(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
