<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    User uf = (User) request.getAttribute("userObj");
    if (uf == null) { response.sendRedirect("UserServlet?action=list"); return; }
    boolean ufIsNew = false;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Edit User | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="UserServlet?action=list">Users</a> / Edit</div>
        <h1>Edit <%= Html.esc(uf.getName()) %></h1>
        <p>Leave the password empty to keep it unchanged.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="UserServlet" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="userID" value="<%= uf.getUserID() %>">
            <%@ include file="/WEB-INF/jspf/userForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="UserServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Save changes</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
