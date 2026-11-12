package ac.za.tut.web;

import ac.za.tut.dao.PropertyDAO;
import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.Property;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Property listings: browse, filter by owner, add, edit and delete.
 */
@WebServlet(name = "PropertyServlet", urlPatterns = {"/PropertyServlet"})
public class PropertyServlet extends HttpServlet {

    private static final String[] TYPES = {"House", "Apartment", "Condo", "Townhouse", "Office"};
    private static final String[] STATUSES = {"Available", "Rented", "Sold"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        PropertyDAO propertyDAO = new PropertyDAO();

        try {
            if ("listOwners".equalsIgnoreCase(action)) {
                List<User> owners = propertyDAO.getAllOwners();
                request.setAttribute("owners", owners);
                request.getRequestDispatcher("PropertyOwnerList.jsp").forward(request, response);

            } else if ("viewByOwner".equalsIgnoreCase(action)) {
                int ownerID = Integer.parseInt(request.getParameter("ownerID"));
                User owner = new UserDAO().getUserById(ownerID);
                request.setAttribute("properties", propertyDAO.getPropertiesByOwner(ownerID));
                request.setAttribute("listTitle", owner != null ? owner.getName() + "'s Portfolio" : "Owner Portfolio");
                request.getRequestDispatcher("PropertyList.jsp").forward(request, response);

            } else if ("view".equalsIgnoreCase(action)) {
                int propertyID = Integer.parseInt(request.getParameter("propertyID"));
                request.setAttribute("property", propertyDAO.getPropertyById(propertyID));
                request.getRequestDispatcher("PropertyDetails.jsp").forward(request, response);

            } else if ("add".equalsIgnoreCase(action)) {
                request.setAttribute("owners", new UserDAO().getUsersByRole("Owner"));
                request.getRequestDispatcher("AddProperty.jsp").forward(request, response);

            } else if ("edit".equalsIgnoreCase(action)) {
                int propertyID = Integer.parseInt(request.getParameter("propertyID"));
                request.setAttribute("propObj", propertyDAO.getPropertyById(propertyID));
                request.setAttribute("owners", new UserDAO().getUsersByRole("Owner"));
                request.getRequestDispatcher("EditProperty.jsp").forward(request, response);

            } else if ("remove".equalsIgnoreCase(action)) {
                int propertyID = Integer.parseInt(request.getParameter("propertyID"));
                try {
                    propertyDAO.deleteProperty(propertyID);
                    response.sendRedirect("PropertyServlet?action=list&success="
                            + URLEncoder.encode("Property #" + propertyID + " was deleted.", "UTF-8"));
                } catch (Exception e) {
                    request.setAttribute("errorMessage", "Cannot delete Property #" + propertyID
                            + " because leases, applications or maintenance records are linked to it.");
                    request.setAttribute("properties", propertyDAO.getAllProperties());
                    request.getRequestDispatcher("PropertyList.jsp").forward(request, response);
                }

            } else {
                request.setAttribute("properties", propertyDAO.getAllProperties());
                request.getRequestDispatcher("PropertyList.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("PropertyServlet?action=list");
        } catch (Exception e) {
            throw new ServletException("Failed to load property data", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        PropertyDAO propertyDAO = new PropertyDAO();
        String formPage = "update".equalsIgnoreCase(action) ? "EditProperty.jsp" : "AddProperty.jsp";

        try {
            String address = trim(request.getParameter("address"));
            String type = request.getParameter("type");
            String status = request.getParameter("status");
            int propertyID = parseInt(request.getParameter("propertyID"), 0);
            int ownerID = parseInt(request.getParameter("ownerID"), -1);
            int size = parseInt(request.getParameter("size"), -1);
            int rooms = parseInt(request.getParameter("rooms"), -1);

            String error = null;
            if (ownerID <= 0) {
                error = "Please choose the property owner.";
            } else if (address.isEmpty() || address.length() > 255) {
                error = "Address is required (maximum 255 characters).";
            } else if (!oneOf(type, TYPES)) {
                error = "Please choose a valid property type.";
            } else if (size <= 0) {
                error = "Size must be a positive number of square metres.";
            } else if (rooms < 0 || rooms > 200) {
                error = "Rooms must be between 0 and 200.";
            } else if (!oneOf(status, STATUSES)) {
                error = "Please choose a valid listing status.";
            }

            Property property = new Property(propertyID, ownerID, address, type, Math.max(size, 0), Math.max(rooms, 0), status);

            if (error != null) {
                request.setAttribute("errorMessage", error);
                request.setAttribute("propObj", property);
                request.setAttribute("owners", new UserDAO().getUsersByRole("Owner"));
                request.getRequestDispatcher(formPage).forward(request, response);
                return;
            }

            String message;
            if ("update".equalsIgnoreCase(action)) {
                propertyDAO.updateProperty(property);
                message = "Property #" + propertyID + " was updated.";
            } else {
                propertyDAO.addProperty(property);
                message = "New property listed at " + address + ".";
            }
            response.sendRedirect("PropertyServlet?action=list&success=" + URLEncoder.encode(message, "UTF-8"));
        } catch (Exception e) {
            throw new ServletException("Property operation failed", e);
        }
    }

    static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    static int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    static boolean oneOf(String value, String[] allowed) {
        if (value == null) {
            return false;
        }
        for (String a : allowed) {
            if (a.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
