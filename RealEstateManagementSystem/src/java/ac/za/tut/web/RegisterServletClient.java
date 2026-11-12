package ac.za.tut.web;

import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrator "Add New Members" form: creates an account with any role.
 * Access is limited to administrators by {@link AuthFilter}.
 */
public class RegisterServletClient extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = PropertyServlet.trim(request.getParameter("name"));
        String role = request.getParameter("role");
        String email = PropertyServlet.trim(request.getParameter("email")).toLowerCase();
        String phone = PropertyServlet.trim(request.getParameter("phone"));
        String password = request.getParameter("password");

        String error = UserServlet.validate(name, role, email, phone);
        if (error == null && (password == null || password.length() < 6)) {
            error = "Password must be at least 6 characters.";
        }

        try {
            UserDAO userDAO = new UserDAO();
            if (error == null && userDAO.emailExists(email)) {
                error = "An account with this email already exists.";
            }
            if (error != null) {
                response.sendRedirect("signupclient.jsp?error=" + URLEncoder.encode(error, "UTF-8"));
                return;
            }
            userDAO.addUser(new User(0, name, role, email, phone, UserDAO.hash(password)));
            response.sendRedirect("signupclient.jsp?success="
                    + URLEncoder.encode(role + " account for " + name + " created successfully!", "UTF-8"));
        } catch (Exception e) {
            response.sendRedirect("signupclient.jsp?error="
                    + URLEncoder.encode("Registration failed: " + e.getMessage(), "UTF-8"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("signupclient.jsp");
    }
}
