package ac.za.tut.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Central access control: every page except the public ones requires a
 * logged-in session, and some pages are limited to particular roles.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    /** Pages anyone may open without logging in. */
    private static final Set<String> PUBLIC = new HashSet<>(Arrays.asList(
            "/", "/index.jsp", "/index.html", "/Login.jsp", "/SignUp.jsp", "/LoginServlet", "/RegisterServlet"));

    /** Administrator-only pages. */
    private static final Set<String> ADMIN_ONLY = new HashSet<>(Arrays.asList(
            "/UserServlet", "/AddUser.jsp", "/EditUser.jsp", "/signupclient.jsp", "/RegisterServletClient"));

    /** Reporting is for administrators, managers and owners. */
    private static final Set<String> REPORTS = new HashSet<>(Arrays.asList(
            "/Reports.jsp", "/ReportServlet", "/EditReport.jsp", "/summary_report.jsp", "/tenant_report.jsp",
            "/application_report.jsp", "/property_maintenance_report.jsp"));

    /** Staff who manage properties, tenants and applications. */
    private static final Set<String> STAFF = new HashSet<>(Arrays.asList(
            "/AddProperty.jsp", "/EditProperty.jsp", "/TenantForm.jsp", "/EditTenant.jsp", "/TenantServlet",
            "/assign_tenant.jsp", "/AssignTenantServlet", "/AddApplication.jsp", "/EditApplication.jsp",
            "/ApplicationList.jsp"));

    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding("UTF-8");

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (PUBLIC.contains(path) || path.startsWith("/css/") || path.startsWith("/images/")
                || path.startsWith("/js/") || path.endsWith(".ico")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        String role = session == null ? null : (String) session.getAttribute("role");
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp?info="
                    + URLEncoder.encode("Please log in to continue.", "UTF-8"));
            return;
        }

        boolean allowed = true;
        if (ADMIN_ONLY.contains(path)) {
            allowed = "Admin".equals(role);
        } else if (REPORTS.contains(path)) {
            if ("Owner".equals(role)) {
                // Owners only get the maintenance report, which is filtered to their own properties.
                String action = request.getParameter("action");
                allowed = ("/ReportServlet".equals(path) || "/property_maintenance_report.jsp".equals(path))
                        && ("maintenance".equalsIgnoreCase(action) || "exportCSV".equalsIgnoreCase(action)
                            || "exportTXT".equalsIgnoreCase(action) || "exportPDF".equalsIgnoreCase(action));
            } else {
                allowed = "Admin".equals(role) || "Manager".equals(role);
            }
        } else if (STAFF.contains(path)) {
            allowed = "Admin".equals(role) || "Manager".equals(role) || "Agent".equals(role);
        } else if ("/ApplicationServlet".equals(path)) {
            // Tenants may only see/submit their own applications.
            String action = request.getParameter("action");
            if ("Tenant".equals(role)) {
                allowed = "myApplications".equalsIgnoreCase(action)
                        || ("POST".equalsIgnoreCase(request.getMethod()) && "add".equalsIgnoreCase(action));
            } else {
                allowed = "Admin".equals(role) || "Manager".equals(role) || "Agent".equals(role);
            }
        } else if ("/PropertyServlet".equals(path)) {
            String action = request.getParameter("action");
            boolean modifies = "POST".equalsIgnoreCase(request.getMethod())
                    || "edit".equalsIgnoreCase(action) || "remove".equalsIgnoreCase(action);
            if (modifies) {
                allowed = "Admin".equals(role) || "Manager".equals(role) || "Agent".equals(role);
            }
        } else if ("/MaintenanceRequestServlet".equals(path)) {
            String action = request.getParameter("action");
            if ("edit".equalsIgnoreCase(action) || "update".equalsIgnoreCase(action)) {
                allowed = "Admin".equals(role) || "Manager".equals(role) || "Maintenance".equals(role);
            } else if ("delete".equalsIgnoreCase(action)) {
                allowed = "Admin".equals(role);
            }
        } else if ("/TenantAddApplication.jsp".equals(path) || "/TenantApplicationList.jsp".equals(path)) {
            allowed = "Tenant".equals(role);
        }

        if (!allowed) {
            response.sendRedirect(request.getContextPath() + "/Welcome.jsp?error="
                    + URLEncoder.encode("You do not have permission to open that page.", "UTF-8"));
            return;
        }

        // Stop browsers caching protected pages so "Back" after logout does not show data.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() { }
}
