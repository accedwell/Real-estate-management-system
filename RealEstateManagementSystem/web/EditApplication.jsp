<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Application af = (Application) request.getAttribute("appObj");
    List<User> afTenants = (List<User>) request.getAttribute("tenants");
    List<Property> afProps = (List<Property>) request.getAttribute("properties");
    if (af == null || afTenants == null || afProps == null) { response.sendRedirect("ApplicationServlet?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Edit Application | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-apps">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="ApplicationServlet?action=list">Applications</a> / Edit</div>
        <h1>Edit application #<%= af.getApplicationID() %></h1>
        <p>Update the applicant, property, date or decision.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="ApplicationServlet" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="applicationID" value="<%= af.getApplicationID() %>">
            <%@ include file="/WEB-INF/jspf/applicationForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="ApplicationServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Save changes</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
