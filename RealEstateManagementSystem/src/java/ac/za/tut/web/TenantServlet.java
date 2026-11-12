package ac.za.tut.web;

import ac.za.tut.dao.PropertyDAO;
import ac.za.tut.dao.TenantDAO;
import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.Tenant;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tenant leases: admit, edit, record departure and delete.
 */
@WebServlet(name = "TenantServlet", urlPatterns = {"/TenantServlet"})
public class TenantServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        TenantDAO tenantDAO = new TenantDAO();

        try {
            if ("form".equalsIgnoreCase(action)) {
                loadFormLists(request);
                request.getRequestDispatcher("TenantForm.jsp").forward(request, response);

            } else if ("edit".equalsIgnoreCase(action)) {
                int tenantID = Integer.parseInt(request.getParameter("tenantID"));
                Tenant tenant = tenantDAO.getTenantById(tenantID);
                if (tenant == null) {
                    showList(request, response, "Lease #" + tenantID + " was not found.");
                    return;
                }
                loadFormLists(request);
                request.setAttribute("tenantObj", tenant);
                request.getRequestDispatcher("EditTenant.jsp").forward(request, response);

            } else if ("remove".equalsIgnoreCase(action)) {
                int tenantID = Integer.parseInt(request.getParameter("tenantID"));
                try {
                    tenantDAO.removeTenant(tenantID);
                    redirect(response, "Lease #" + tenantID + " was deleted.");
                } catch (Exception e) {
                    showList(request, response, "Cannot delete Lease #" + tenantID
                            + " because maintenance requests are linked to it. Use \"Record departure\" instead.");
                }

            } else {
                showList(request, response, null);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("TenantServlet?action=list");
        } catch (Exception e) {
            throw new ServletException("Tenant GET operation failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        TenantDAO tenantDAO = new TenantDAO();
        PropertyDAO propertyDAO = new PropertyDAO();
        boolean isUpdate = "update".equalsIgnoreCase(action);

        try {
            int tenantID = PropertyServlet.parseInt(request.getParameter("tenantID"), 0);
            int userID = PropertyServlet.parseInt(request.getParameter("userID"), 0);
            int propertyID = PropertyServlet.parseInt(request.getParameter("propertyID"), 0);

            if (userID <= 0 || propertyID <= 0) {
                backToForm(request, response, isUpdate, "Please choose both a tenant and a property.");
                return;
            }

            if ("leave".equalsIgnoreCase(action)) {
                int closed = tenantDAO.endLease(userID, propertyID);
                if (closed == 0) {
                    backToForm(request, response, false, "That tenant has no active lease on the selected property.");
                    return;
                }
                propertyDAO.updateStatus(propertyID, "Available");
                redirect(response, "Departure recorded. The property is available again.");
                return;
            }

            Date leaseStart;
            Date leaseEnd = null;
            try {
                leaseStart = Date.valueOf(request.getParameter("leaseStart"));
                String end = request.getParameter("leaseEnd");
                if (end != null && !end.isEmpty()) {
                    leaseEnd = Date.valueOf(end);
                }
            } catch (IllegalArgumentException e) {
                backToForm(request, response, isUpdate, "Please enter a valid lease start date.");
                return;
            }
            if (leaseEnd != null && leaseEnd.before(leaseStart)) {
                backToForm(request, response, isUpdate, "The lease end date cannot be before the start date.");
                return;
            }

            Tenant tenant = new Tenant(tenantID, userID, propertyID, leaseStart, leaseEnd);
            if (isUpdate) {
                tenantDAO.updateTenant(tenant);
                redirect(response, "Lease #" + tenantID + " was updated.");
            } else {
                tenantDAO.addTenant(tenant);
                propertyDAO.updateStatus(propertyID, "Rented");
                redirect(response, "Tenant admitted. The property is now marked as Rented.");
            }
        } catch (Exception e) {
            throw new ServletException("Tenant POST operation failed", e);
        }
    }

    private void loadFormLists(HttpServletRequest request) throws Exception {
        request.setAttribute("users", new UserDAO().getUsersByRole("Tenant"));
        request.setAttribute("properties", new PropertyDAO().getAllProperties());
    }

    private void showList(HttpServletRequest request, HttpServletResponse response, String error)
            throws Exception {
        if (error != null) {
            request.setAttribute("errorMessage", error);
        }
        request.setAttribute("leases", new TenantDAO().getLeaseDetails(null));
        request.getRequestDispatcher("TenantList.jsp").forward(request, response);
    }

    private void backToForm(HttpServletRequest request, HttpServletResponse response, boolean isUpdate, String error)
            throws Exception {
        request.setAttribute("errorMessage", error);
        loadFormLists(request);
        if (isUpdate) {
            int tenantID = PropertyServlet.parseInt(request.getParameter("tenantID"), 0);
            request.setAttribute("tenantObj", new TenantDAO().getTenantById(tenantID));
            request.getRequestDispatcher("EditTenant.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("TenantForm.jsp").forward(request, response);
        }
    }

    private void redirect(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect("TenantServlet?action=list&success=" + URLEncoder.encode(message, "UTF-8"));
    }
}
