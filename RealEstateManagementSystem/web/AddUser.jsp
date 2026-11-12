<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    User uf = (User) request.getAttribute("userObj");
    boolean ufIsNew = true;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Add User | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="UserServlet?action=list">Users</a> / New</div>
        <h1>Register a new user</h1>
        <p>Create an account for any role. Share the temporary password with the user securely.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="UserServlet" method="POST">
            <input type="hidden" name="action" value="add">
            <%@ include file="/WEB-INF/jspf/userForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="UserServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Create user</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
