package ac.za.tut.web;

import ac.za.tut.dao.MaintenanceRequestDAO;
import ac.za.tut.dao.TenantDAO;
import ac.za.tut.entities.Tenant;
import ac.za.tut.model.MaintenanceRequest;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Maintenance requests. A request is always logged against a lease
 * (Tenants.tenantID); the property is taken from that lease.
 */
public class MaintenanceRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

        try {
            if ("new".equalsIgnoreCase(action)) {
                showForm(request, response, null);

            } else if ("edit".equalsIgnoreCase(action)) {
                int requestID = Integer.parseInt(request.getParameter("requestID"));
                MaintenanceRequest req = dao.getRequestById(requestID);
                if (req == null) {
                    response.sendRedirect("MaintenanceRequestServlet?action=list");
                    return;
                }
                request.setAttribute("requestObj", req);
                request.getRequestDispatcher("EditMaintenance.jsp").forward(request, response);

            } else if ("delete".equalsIgnoreCase(action)) {
                int requestID = Integer.parseInt(request.getParameter("requestID"));
                dao.deleteRequest(requestID);
                redirect(response, "Request #" + requestID + " was deleted.");

            } else {
                HttpSession session = request.getSession(false);
                String role = (String) session.getAttribute("role");
                Integer filter = "Tenant".equals(role) ? (Integer) session.getAttribute("userID") : null;
                request.setAttribute("requests", dao.getRequestDetails(filter));
                request.getRequestDispatcher("MaintenanceList.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("MaintenanceRequestServlet?action=list");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load requests: " + e.getMessage());
            request.getRequestDispatcher("MaintenanceList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        int sessionUser = (Integer) session.getAttribute("userID");

        try {
            String description = PropertyServlet.trim(request.getParameter("description"));
            String priority = request.getParameter("priority");
            String notes = PropertyServlet.trim(request.getParameter("resolutionNotes"));

            if (!PropertyServlet.oneOf(priority, new String[]{"Low", "Medium", "High"})) {
                priority = "Medium";
            }
            if (notes.length() > 255) {
                notes = notes.substring(0, 255);
            }

            if ("update".equalsIgnoreCase(action)) {
                int requestID = Integer.parseInt(request.getParameter("requestID"));
                MaintenanceRequest existing = dao.getRequestById(requestID);
                if (existing == null) {
                    response.sendRedirect("MaintenanceRequestServlet?action=list");
                    return;
                }
                String status = request.getParameter("status");
                if (!PropertyServlet.oneOf(status, new String[]{"Pending", "InProgress", "Resolved"})) {
                    status = existing.getStatus();
                }
                double cost;
                try {
                    cost = Double.parseDouble(request.getParameter("cost"));
                } catch (Exception e) {
                    cost = -1;
                }
                if (cost < 0) {
                    request.setAttribute("errorMessage", "Cost must be zero or a positive amount.");
                    request.setAttribute("requestObj", existing);
                    request.getRequestDispatcher("EditMaintenance.jsp").forward(request, response);
                    return;
                }
                existing.setPriority(priority);
                existing.setStatus(status);
                existing.setCost(cost);
                existing.setResolutionNotes(notes);
                dao.updateRequest(existing);
                redirect(response, "Request #" + requestID + " was updated.");
                return;
            }

            // ---- New request
            int tenantID = PropertyServlet.parseInt(request.getParameter("tenantID"), 0);
            Tenant lease = tenantID > 0 ? new TenantDAO().getTenantById(tenantID) : null;
            if (lease == null) {
                showForm(request, response, "Please choose the lease (tenant and property) this request is for.");
                return;
            }
            if ("Tenant".equals(role) && lease.getUserID() != sessionUser) {
                showForm(request, response, "You can only log requests for your own lease.");
                return;
            }
            if (description.isEmpty() || description.length() > 255) {
                showForm(request, response, "Please describe the issue (maximum 255 characters).");
                return;
            }

            MaintenanceRequest req = new MaintenanceRequest(0, tenantID, lease.getPropertyID(),
                    description, priority, "Pending", 0.0, notes);
            dao.addRequest(req);
            redirect(response, "Maintenance request logged. Our team will be in touch.");

        } catch (NumberFormatException e) {
            showForm(request, response, "Invalid numeric input.");
        } catch (Exception e) {
            showForm(request, response, "Maintenance request failed: " + e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            String role = (String) session.getAttribute("role");
            Integer filter = "Tenant".equals(role) ? (Integer) session.getAttribute("userID") : null;
            request.setAttribute("leases", new TenantDAO().getLeaseDetails(filter));
        } catch (Exception e) {
            error = (error == null ? "" : error + " ") + "Could not load leases: " + e.getMessage();
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
        }
        request.getRequestDispatcher("MaintenanceRequest.jsp").forward(request, response);
    }

    private void redirect(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect("MaintenanceRequestServlet?action=list&success=" + URLEncoder.encode(message, "UTF-8"));
    }

    @Override
    public String getServletInfo() {
        return "Maintenance Request Servlet";
    }
}
