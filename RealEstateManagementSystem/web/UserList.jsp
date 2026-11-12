<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.User"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) { response.sendRedirect("UserServlet?action=list"); return; }
    Integer me = (Integer) session.getAttribute("userID");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Users | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Users</div>
        <h1>System Users</h1>
        <p><%= users.size() %> accounts across all roles.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <div class="card">
        <div class="card-head">
            <input type="text" id="q" placeholder="Search name, email or role&hellip;" style="max-width:320px" oninput="filterRows('q','rows')">
            <a href="AddUser.jsp" class="btn btn-primary"><%= Icon.svg("plus") %> Add user</a>
        </div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>ID</th><th>Name</th><th>Role</th><th>Email</th><th>Phone</th><th></th></tr></thead>
                <tbody id="rows">
                <% for (User u : users) { %>
                    <tr>
                        <td><strong>#<%= u.getUserID() %></strong></td>
                        <td><div style="display:flex;align-items:center;gap:10px"><span class="avatar"><%= Html.initial(u.getName()) %></span><%= Html.esc(u.getName()) %><%= (me != null && me == u.getUserID()) ? " <span class='small muted'>(you)</span>" : "" %></div></td>
                        <td><span class="badge <%= "Admin".equals(u.getRole()) ? "badge-sold" : "badge-active" %>"><%= Html.esc(u.getRole()) %></span></td>
                        <td><%= Html.esc(u.getEmail()) %></td>
                        <td><%= Html.escOr(u.getPhone(), "—") %></td>
                        <td><div class="actions">
                            <a class="btn btn-sm btn-edit" href="UserServlet?action=edit&userID=<%= u.getUserID() %>"><%= Icon.svg("pencil") %> Edit</a>
                            <% if (me == null || me != u.getUserID()) { %>
                            <a class="btn btn-sm btn-danger" href="UserServlet?action=remove&userID=<%= u.getUserID() %>" onclick="return confirm('Delete this user?')"><%= Icon.svg("trash") %> Delete</a>
                            <% } %>
                        </div></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
</body>
</html>
