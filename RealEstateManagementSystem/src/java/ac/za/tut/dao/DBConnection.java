package ac.za.tut.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens PostgreSQL connections.
 *
 * Defaults to a local database. To point at another server without
 * recompiling, set the environment variables (or JVM -D properties)
 * REMS_DB_URL, REMS_DB_USER and REMS_DB_PASSWORD.
 */
public class DBConnection {

    private static final String URL = setting("REMS_DB_URL",
            "jdbc:postgresql://localhost:5432/RealEstateManagementSystem");
    private static final String USER = setting("REMS_DB_USER", "postgres");
    private static final String PASSWORD = setting("REMS_DB_PASSWORD", "123456");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }
    }

    private static String setting(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
