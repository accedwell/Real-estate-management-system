<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.dao.PropertyDAO"%>
<%@page import="ac.za.tut.dao.UserDAO"%>
<%
    Application af = (Application) request.getAttribute("appObj");
    List<User> afTenants = (List<User>) request.getAttribute("tenants");
    List<Property> afProps = (List<Property>) request.getAttribute("properties");
    if (afTenants == null) { afTenants = new UserDAO().getUsersByRole("Tenant"); }
    if (afProps == null) { afProps = new PropertyDAO().getAllProperties(); }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>New Application | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-apps">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="ApplicationServlet?action=list">Applications</a> / New</div>
        <h1>Capture a rental application</h1>
        <p>Record an application on behalf of a tenant.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="ApplicationServlet" method="POST">
            <input type="hidden" name="action" value="add">
            <%@ include file="/WEB-INF/jspf/applicationForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="ApplicationServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Submit application</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
</body>
</html>
