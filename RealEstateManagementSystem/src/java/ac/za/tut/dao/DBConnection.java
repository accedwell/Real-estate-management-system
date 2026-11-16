package ac.za.tut.dao;

import java.net.URI;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens PostgreSQL connections.
 *
 * Settings are read from environment variables (or JVM -D properties), in this order:
 * <ol>
 *   <li>REMS_DB_URL, REMS_DB_USER, REMS_DB_PASSWORD (a JDBC URL plus credentials), or</li>
 *   <li>DATABASE_URL in the form postgresql://user:password@host:port/database
 *       (what hosts such as Render, Railway and Heroku provide), or</li>
 *   <li>the local default: jdbc:postgresql://localhost:5432/RealEstateManagementSystem as postgres / 123456.</li>
 * </ol>
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }

        String url = setting("REMS_DB_URL", null);
        String user = setting("REMS_DB_USER", null);
        String password = setting("REMS_DB_PASSWORD", null);
        String databaseUrl = setting("DATABASE_URL", null);

        if (url == null && databaseUrl != null) {
            String[] parsed = fromDatabaseUrl(databaseUrl);
            url = parsed[0];
            if (user == null) user = parsed[1];
            if (password == null) password = parsed[2];
        }
        URL = url != null ? url : "jdbc:postgresql://localhost:5432/RealEstateManagementSystem";
        USER = user != null ? user : "postgres";
        PASSWORD = password != null ? password : "123456";
    }

    private static String setting(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    /** Turns postgres(ql)://user:pass@host:port/db?params into {jdbcUrl, user, password}. */
    static String[] fromDatabaseUrl(String databaseUrl) {
        try {
            URI uri = new URI(databaseUrl.trim());
            String user = null, password = null;
            String info = uri.getRawUserInfo();
            if (info != null) {
                int colon = info.indexOf(':');
                user = URLDecoder.decode(colon >= 0 ? info.substring(0, colon) : info, "UTF-8");
                password = colon >= 0 ? URLDecoder.decode(info.substring(colon + 1), "UTF-8") : null;
            }
            String jdbc = "jdbc:postgresql://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "")
                    + (uri.getRawPath() == null ? "" : uri.getRawPath())
                    + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            return new String[]{jdbc, user, password};
        } catch (Exception e) {
            throw new IllegalArgumentException("DATABASE_URL is not a valid postgresql:// URL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
