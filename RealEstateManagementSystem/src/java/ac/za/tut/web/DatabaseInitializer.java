package ac.za.tut.web;

import ac.za.tut.dao.DBConnection;
import ac.za.tut.dao.UserDAO;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * First-run setup for hosted deployments.
 *
 * When the app starts and the database has no Users table yet, it runs the
 * bundled schema script (/WEB-INF/db/schema.sql) to create the tables and
 * demo data. An existing database is never touched. If the environment
 * variable REMS_ADMIN_PASSWORD is set, the admin@rems.co.za password is set
 * to it during that first-run setup, so a public site does not keep the
 * well-known demo password for the administrator.
 *
 * Local installs that have no bundled script simply skip this step.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final String SCRIPT = "/WEB-INF/db/schema.sql";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();
        try (InputStream in = ctx.getResourceAsStream(SCRIPT)) {
            if (in == null) {
                return; // no bundled script: nothing to do (normal for local NetBeans runs)
            }
            try (Connection conn = DBConnection.getConnection()) {
                if (tableExists(conn, "users")) {
                    ctx.log("REMS: database already set up; skipping first-run script.");
                    return;
                }
                ctx.log("REMS: empty database detected; creating tables and demo data...");
                try (Statement st = conn.createStatement()) {
                    st.execute(read(in));
                }
                String adminPassword = System.getenv("REMS_ADMIN_PASSWORD");
                if (adminPassword != null && adminPassword.length() >= 6) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE Users SET passwordHash = ? WHERE LOWER(email) = 'admin@rems.co.za'")) {
                        ps.setBytes(1, UserDAO.hash(adminPassword));
                        ps.executeUpdate();
                    }
                    ctx.log("REMS: administrator password set from REMS_ADMIN_PASSWORD.");
                }
                ctx.log("REMS: database ready.");
            }
        } catch (Exception e) {
            // Never stop the app from starting; pages will show a friendly database error instead.
            ctx.log("REMS: first-run database setup failed: " + e.getMessage(), e);
        }
    }

    private static boolean tableExists(Connection conn, String table) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM information_schema.tables WHERE table_schema = current_schema() AND LOWER(table_name) = ?")) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String read(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // nothing to clean up
    }
}
