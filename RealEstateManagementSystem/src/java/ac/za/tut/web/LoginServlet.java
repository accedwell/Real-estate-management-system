package ac.za.tut.web;

import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authenticates a user by email + SHA-256 password hash and starts a session.
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("username");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect("Login.jsp?error=" + URLEncoder.encode("Please enter your email and password.", "UTF-8"));
            return;
        }

        try {
            User user = new UserDAO().authenticate(email, password);

            if (user != null) {
                // Prevent session fixation: discard any existing session first.
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession(true);
                session.setAttribute("userID", user.getUserID());
                session.setAttribute("role", user.getRole());
                session.setAttribute("name", user.getName());
                session.setAttribute("email", user.getEmail());
                response.sendRedirect("Welcome.jsp");
            } else {
                response.sendRedirect("Login.jsp?error="
                        + URLEncoder.encode("Invalid email or password. Please try again.", "UTF-8")
                        + "&email=" + URLEncoder.encode(email.trim(), "UTF-8"));
            }
        } catch (Exception e) {
            log("Login failed", e);
            response.sendRedirect("Login.jsp?error="
                    + URLEncoder.encode("System error occurred. Please contact the administrator.", "UTF-8"));
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user login";
    }
}
