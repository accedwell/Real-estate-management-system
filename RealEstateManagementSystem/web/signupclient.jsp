<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    User uf = null;
    boolean ufIsNew = true;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Add New Member | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Add member</div>
        <h1>Add a new member</h1>
        <p>Create staff, owner or tenant accounts. You stay on this page so you can add several in a row.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="RegisterServletClient" method="POST">
            <%@ include file="/WEB-INF/jspf/userForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="UserServlet?action=list">View all users</a>
                <button type="submit" class="btn btn-dark">Create account</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
