<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.dao.UserDAO"%>
<%
    Property pf = (Property) request.getAttribute("propObj");
    if (pf == null) { response.sendRedirect("PropertyServlet?action=list"); return; }
    List<User> pfOwners = (List<User>) request.getAttribute("owners");
    if (pfOwners == null) { pfOwners = new UserDAO().getUsersByRole("Owner"); }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Edit Property | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-props">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="PropertyServlet?action=list">Properties</a> / Edit</div>
        <h1>Edit property</h1>
        <p><%= Html.esc(pf.getAddress()) %></p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="PropertyServlet" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="propertyID" value="<%= pf.getPropertyID() %>">
            <%@ include file="/WEB-INF/jspf/propertyForm.jspf" %>
            <div class="form-actions">
                <a class="btn btn-outline" href="PropertyServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Update property</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
