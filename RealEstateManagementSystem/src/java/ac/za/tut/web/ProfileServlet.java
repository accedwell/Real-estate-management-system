package ac.za.tut.web;

import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * "My profile" for every logged-in user: view account details, update
 * name and phone number, and change password. Users can only ever edit
 * their own account (the ID comes from the session, never the request).
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/ProfileServlet"})
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userID = currentUser(request);
        if (userID == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        try {
            User user = new UserDAO().getUserById(userID);
            if (user == null) {
                response.sendRedirect("LogoutServlet");
                return;
            }
            request.setAttribute("user", user);
            request.getRequestDispatcher("Profile.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("Welcome.jsp?error=" + enc("Your profile could not be loaded. Please try again."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userID = currentUser(request);
        if (userID == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        String action = request.getParameter("action");
        try {
            UserDAO dao = new UserDAO();
            User user = dao.getUserById(userID);
            if (user == null) {
                response.sendRedirect("LogoutServlet");
                return;
            }

            if ("password".equals(action)) {
                String current = request.getParameter("current");
                String next = request.getParameter("password");
                String confirm = request.getParameter("confirm");
                if (current == null || !MessageDigest.isEqual(UserDAO.hash(current), user.getPasswordHash())) {
                    back(response, "error", "Your current password is incorrect.", "security");
                } else if (next == null || next.length() < 6) {
                    back(response, "error", "Your new password must be at least 6 characters.", "security");
                } else if (!next.equals(confirm)) {
                    back(response, "error", "The two new passwords do not match.", "security");
                } else {
                    dao.updatePassword(userID, UserDAO.hash(next));
                    back(response, "success", "Your password has been changed.", "security");
                }
                return;
            }

            // Default: update personal details (role and email stay as they are).
            String name = PropertyServlet.trim(request.getParameter("name"));
            String phone = PropertyServlet.trim(request.getParameter("phone"));
            String error = UserServlet.validate(name, user.getRole(), user.getEmail(), phone);
            if (error != null) {
                back(response, "error", error, "details");
                return;
            }
            user.setName(name);
            user.setPhone(phone);
            dao.updateUser(user);
            request.getSession().setAttribute("name", name);
            back(response, "success", "Your details have been saved.", "details");
        } catch (Exception e) {
            back(response, "error", "Something went wrong. Please try again.", "details");
        }
    }

    private static Integer currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object id = session == null ? null : session.getAttribute("userID");
        return id instanceof Integer ? (Integer) id : null;
    }

    private static void back(HttpServletResponse response, String kind, String message, String tab) throws IOException {
        response.sendRedirect("ProfileServlet?" + kind + "=" + enc(message) + "#" + tab);
    }

    private static String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
