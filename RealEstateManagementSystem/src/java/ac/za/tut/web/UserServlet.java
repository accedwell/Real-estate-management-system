package ac.za.tut.web;

import ac.za.tut.dao.UserDAO;
import ac.za.tut.entities.User;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrator user management: list, add, edit, reset password, delete.
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/UserServlet"})
public class UserServlet extends HttpServlet {

    static final String[] ROLES = {"Owner", "Tenant", "Agent", "Manager", "Maintenance", "Admin"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        try {
            if ("edit".equalsIgnoreCase(action)) {
                int userID = Integer.parseInt(request.getParameter("userID"));
                request.setAttribute("userObj", dao.getUserById(userID));
                request.getRequestDispatcher("EditUser.jsp").forward(request, response);

            } else if ("remove".equalsIgnoreCase(action)) {
                int userID = Integer.parseInt(request.getParameter("userID"));
                Integer me = (Integer) request.getSession().getAttribute("userID");
                if (me != null && me == userID) {
                    showList(request, response, "You cannot delete the account you are logged in with.");
                    return;
                }
                try {
                    dao.removeUser(userID);
                    redirect(response, "User #" + userID + " was deleted.");
                } catch (Exception e) {
                    showList(request, response, "Cannot delete User #" + userID
                            + " because they are linked to properties, leases, applications or reports.");
                }

            } else {
                showList(request, response, null);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("UserServlet?action=list");
        } catch (Exception e) {
            throw new ServletException("User GET operation failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();
        boolean isUpdate = "update".equalsIgnoreCase(action);

        try {
            int userID = PropertyServlet.parseInt(request.getParameter("userID"), 0);
            String name = PropertyServlet.trim(request.getParameter("name"));
            String role = request.getParameter("role");
            String email = PropertyServlet.trim(request.getParameter("email")).toLowerCase();
            String phone = PropertyServlet.trim(request.getParameter("phone"));
            String password = request.getParameter("password");

            User user = new User(userID, name, role, email, phone, new byte[0]);
            String error = validate(name, role, email, phone);
            if (error == null && !isUpdate && (password == null || password.length() < 6)) {
                error = "Password must be at least 6 characters.";
            }
            if (error == null && isUpdate && password != null && !password.isEmpty() && password.length() < 6) {
                error = "A new password must be at least 6 characters.";
            }
            if (error == null) {
                User existing = isUpdate ? dao.getUserById(userID) : null;
                boolean emailChanged = !isUpdate || existing == null || !email.equalsIgnoreCase(existing.getEmail());
                if (emailChanged && dao.emailExists(email)) {
                    error = "Another account already uses " + email + ".";
                }
            }

            if (error != null) {
                request.setAttribute("errorMessage", error);
                request.setAttribute("userObj", user);
                request.getRequestDispatcher(isUpdate ? "EditUser.jsp" : "AddUser.jsp").forward(request, response);
                return;
            }

            if (isUpdate) {
                dao.updateUser(user);
                if (password != null && !password.isEmpty()) {
                    dao.updatePassword(userID, UserDAO.hash(password));
                }
                redirect(response, name + "'s account was updated.");
            } else {
                user.setPasswordHash(UserDAO.hash(password));
                dao.addUser(user);
                redirect(response, "Account created for " + name + " (" + role + ").");
            }
        } catch (Exception e) {
            throw new ServletException("User POST operation failed", e);
        }
    }

    /** Shared validation for every account form. Returns an error message or null. */
    static String validate(String name, String role, String email, String phone) {
        if (name == null || name.isEmpty() || name.length() > 100) {
            return "Full name is required (maximum 100 characters).";
        }
        if (!PropertyServlet.oneOf(role, ROLES)) {
            return "Please choose a valid role.";
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || email.length() > 100) {
            return "Please enter a valid email address.";
        }
        if (phone != null && !phone.isEmpty() && !phone.matches("^[+0-9 ()-]{7,20}$")) {
            return "Phone number may contain digits, spaces, +, - and brackets (7-20 characters).";
        }
        return null;
    }

    private void showList(HttpServletRequest request, HttpServletResponse response, String error) throws Exception {
        if (error != null) {
            request.setAttribute("errorMessage", error);
        }
        request.setAttribute("users", new UserDAO().getAllUsers());
        request.getRequestDispatcher("UserList.jsp").forward(request, response);
    }

    private void redirect(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect("UserServlet?action=list&success=" + URLEncoder.encode(message, "UTF-8"));
    }
}
