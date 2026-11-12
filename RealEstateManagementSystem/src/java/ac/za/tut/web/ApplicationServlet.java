package ac.za.tut.web;

import ac.za.tut.dao.ApplicationDAO;
import ac.za.tut.dao.PropertyDAO;
import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.Application;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Rental applications: tenants apply, staff review (approve / reject), edit or delete.
 */
@WebServlet(name = "ApplicationServlet", urlPatterns = {"/ApplicationServlet"})
public class ApplicationServlet extends HttpServlet {

    private static final String[] STATUSES = {"Pending", "Approved", "Rejected"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        ApplicationDAO dao = new ApplicationDAO();

        try {
            if ("new".equalsIgnoreCase(action)) {
                loadFormLists(request);
                request.getRequestDispatcher("AddApplication.jsp").forward(request, response);

            } else if ("edit".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("applicationID"));
                request.setAttribute("appObj", dao.getApplicationById(id));
                loadFormLists(request);
                request.getRequestDispatcher("EditApplication.jsp").forward(request, response);

            } else if ("approve".equalsIgnoreCase(action) || "reject".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("applicationID"));
                String status = "approve".equalsIgnoreCase(action) ? "Approved" : "Rejected";
                dao.updateStatus(id, status);
                redirect(response, "list", "Application #" + id + " " + status.toLowerCase() + ".");

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("applicationID"));
                dao.removeApplication(id);
                redirect(response, "list", "Application #" + id + " was deleted.");

            } else if ("myApplications".equalsIgnoreCase(action)) {
                HttpSession session = request.getSession(false);
                int userID = (Integer) session.getAttribute("userID");
                request.setAttribute("myApplications", dao.getApplicationDetails(userID));
                request.getRequestDispatcher("TenantApplicationList.jsp").forward(request, response);

            } else {
                request.setAttribute("applications", dao.getApplicationDetails(null));
                request.getRequestDispatcher("ApplicationList.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ApplicationServlet?action=list");
        } catch (Exception e) {
            throw new ServletException("Failed to load applications", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        ApplicationDAO dao = new ApplicationDAO();
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        boolean isTenant = "Tenant".equals(role);

        int tenantID = PropertyServlet.parseInt(request.getParameter("tenantID"), 0);
        int propertyID = PropertyServlet.parseInt(request.getParameter("propertyID"), 0);
        String dateStr = request.getParameter("date");
        String status = request.getParameter("status");

        // Tenants always apply as themselves, and a new application is always Pending.
        if (isTenant) {
            tenantID = (Integer) session.getAttribute("userID");
            status = "Pending";
        }

        String error = null;
        java.sql.Date date = null;
        if (tenantID <= 0 || propertyID <= 0) {
            error = "Please choose both an applicant and a property.";
        } else if (!PropertyServlet.oneOf(status, STATUSES)) {
            error = "Please choose a valid status.";
        } else {
            try {
                date = java.sql.Date.valueOf(dateStr);
            } catch (Exception e) {
                error = "Please enter a valid application date.";
            }
        }

        try {
            if (error == null && new PropertyDAO().getPropertyById(propertyID) == null) {
                error = "Property #" + propertyID + " does not exist.";
            }

            int applicationID = PropertyServlet.parseInt(request.getParameter("applicationID"), 0);
            Application application = new Application(applicationID, tenantID, propertyID, date, status);

            if (error != null) {
                request.setAttribute("errorMessage", error);
                request.setAttribute("appObj", application);
                loadFormLists(request);
                String page = isTenant ? "TenantAddApplication.jsp"
                        : ("update".equalsIgnoreCase(action) ? "EditApplication.jsp" : "AddApplication.jsp");
                request.getRequestDispatcher(page).forward(request, response);
                return;
            }

            if ("update".equalsIgnoreCase(action) && !isTenant) {
                dao.updateApplication(application);
                redirect(response, "list", "Application #" + applicationID + " was updated.");
            } else {
                dao.addApplication(application);
                redirect(response, isTenant ? "myApplications" : "list",
                        "Application submitted. Status: " + status + ".");
            }
        } catch (Exception e) {
            throw new ServletException("Application operation failed", e);
        }
    }

    private void loadFormLists(HttpServletRequest request) throws Exception {
        request.setAttribute("tenants", new UserDAO().getUsersByRole("Tenant"));
        request.setAttribute("properties", new PropertyDAO().getAllProperties());
    }

    private void redirect(HttpServletResponse response, String action, String message) throws IOException {
        response.sendRedirect("ApplicationServlet?action=" + action + "&success=" + URLEncoder.encode(message, "UTF-8"));
    }
}
