package ac.za.tut.web;

import ac.za.tut.dao.PropertyDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * "My properties" view: owners see the properties they own,
 * tenants see the properties they lease. Everyone else sees all listings.
 */
public class PropertyListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            String role = (String) session.getAttribute("role");
            int userID = (Integer) session.getAttribute("userID");
            PropertyDAO dao = new PropertyDAO();

            if ("Owner".equals(role)) {
                request.setAttribute("myProperties", dao.getPropertiesByOwner(userID));
                request.setAttribute("myTitle", "My Owned Properties");
                request.getRequestDispatcher("MyProperty.jsp").forward(request, response);
            } else if ("Tenant".equals(role)) {
                request.setAttribute("myProperties", dao.getRentedProperties(userID));
                request.setAttribute("myTitle", "My Rented Homes");
                request.getRequestDispatcher("MyProperty.jsp").forward(request, response);
            } else {
                request.setAttribute("properties", dao.getAllProperties());
                request.getRequestDispatcher("PropertyList.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Data retrieval failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
