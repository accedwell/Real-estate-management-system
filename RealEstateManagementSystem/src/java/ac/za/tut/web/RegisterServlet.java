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
 * Public self-registration. Only Tenant and Owner accounts can be created
 * here; staff accounts are created by an administrator.
 */
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = PropertyServlet.trim(request.getParameter("name"));
        // Public sign-up always creates a Tenant; owners and staff are added by an administrator.
        String role = "Tenant";
        String email = PropertyServlet.trim(request.getParameter("email")).toLowerCase();
        String phone = PropertyServlet.trim(request.getParameter("phone"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        String error = UserServlet.validate(name, role, email, phone);
        if (error == null && (password == null || password.length() < 6)) {
            error = "Password must be at least 6 characters.";
        }
        if (error == null && confirm != null && !confirm.equals(password)) {
            error = "The two passwords do not match.";
        }

        try {
            UserDAO userDAO = new UserDAO();
            if (error == null && userDAO.emailExists(email)) {
                error = "An account with this email already exists. Try logging in instead.";
            }
            if (error != null) {
                back(response, error, name, email, phone, role);
                return;
            }

            User newUser = new User(0, name, role, email, phone, UserDAO.hash(password));
            userDAO.addUser(newUser);
            response.sendRedirect("Login.jsp?success="
                    + URLEncoder.encode("Account created successfully! Please log in.", "UTF-8"));
        } catch (Exception e) {
            back(response, "Registration failed. Please try again later.", name, email, phone, role);
        }
    }

    private void back(HttpServletResponse response, String error, String name, String email, String phone, String role)
            throws IOException {
        response.sendRedirect("SignUp.jsp?error=" + URLEncoder.encode(error, "UTF-8")
                + "&name=" + URLEncoder.encode(name == null ? "" : name, "UTF-8")
                + "&email=" + URLEncoder.encode(email == null ? "" : email, "UTF-8")
                + "&phone=" + URLEncoder.encode(phone == null ? "" : phone, "UTF-8")
                + "&role=" + URLEncoder.encode(role == null ? "" : role, "UTF-8"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("SignUp.jsp");
    }
}
