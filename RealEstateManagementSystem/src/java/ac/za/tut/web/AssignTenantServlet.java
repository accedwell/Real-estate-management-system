package ac.za.tut.web;

import ac.za.tut.dao.PropertyDAO;
import ac.za.tut.dao.TenantDAO;
import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.Property;
import ac.za.tut.entities.Tenant;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Assigns a lease to a tenant with full validation (empty fields,
 * numeric/date formats, field lengths) and saves it to the database.
 */
public class AssignTenantServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("assign_tenant.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tenantIDStr = request.getParameter("tenantID");
        String propertyIDStr = request.getParameter("propertyID");
        String leaseStartStr = request.getParameter("leaseStart");
        String leaseEndStr = request.getParameter("leaseEnd");
        String notes = request.getParameter("notes");

        // 1. Empty fields
        if (isBlank(tenantIDStr) || isBlank(propertyIDStr) || isBlank(leaseStartStr) || isBlank(leaseEndStr)) {
            sendError(request, response, "All fields (except notes) are required. ID and date fields cannot be empty.");
            return;
        }

        // 2. Numeric and date formats
        int tenantID;
        int propertyID;
        LocalDate startDate;
        LocalDate endDate;
        try {
            tenantID = Integer.parseInt(tenantIDStr.trim());
            propertyID = Integer.parseInt(propertyIDStr.trim());
            if (tenantID <= 0 || propertyID <= 0) {
                throw new NumberFormatException();
            }
            startDate = LocalDate.parse(leaseStartStr);
            endDate = LocalDate.parse(leaseEndStr);
        } catch (NumberFormatException e) {
            sendError(request, response, "Tenant ID and Property ID must be positive whole numbers, not text.");
            return;
        } catch (DateTimeParseException e) {
            sendError(request, response, "Please select valid lease dates.");
            return;
        }
        if (endDate.isBefore(startDate)) {
            sendError(request, response, "The lease end date cannot be before the lease start date.");
            return;
        }

        // 3. Field lengths
        if (notes != null && notes.length() > 100) {
            sendError(request, response, "Lease notes are too long (" + notes.length()
                    + " characters). Maximum allowed is 100 characters.");
            return;
        }

        // 4. Referential checks, then save
        try {
            User user = new UserDAO().getUserById(tenantID);
            if (user == null || !"Tenant".equals(user.getRole())) {
                sendError(request, response, "User #" + tenantID + " does not exist or is not a Tenant.");
                return;
            }
            PropertyDAO propertyDAO = new PropertyDAO();
            Property property = propertyDAO.getPropertyById(propertyID);
            if (property == null) {
                sendError(request, response, "Property #" + propertyID + " does not exist.");
                return;
            }

            new TenantDAO().addTenant(new Tenant(0, tenantID, propertyID,
                    java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)));
            propertyDAO.updateStatus(propertyID, "Rented");

            request.setAttribute("successMessage", user.getName() + " was assigned to "
                    + property.getAddress() + " (" + startDate + " to " + endDate + ").");
            request.getRequestDispatcher("assign_tenant.jsp").forward(request, response);
        } catch (Exception e) {
            sendError(request, response, "Could not save the lease: " + e.getMessage());
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("assign_tenant.jsp").forward(request, response);
    }
}
