<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> apps = (List<Map<String, String>>) request.getAttribute("myApplications");
    if (apps == null) { response.sendRedirect("ApplicationServlet?action=myApplications"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>My Applications | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-apps">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / My applications</div>
        <h1>My Rental Applications</h1>
        <p>Follow the progress of every application you have submitted.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <div class="card">
        <div class="card-head">
            <h2>Applications</h2>
            <a href="TenantAddApplication.jsp" class="btn btn-primary"><%= Icon.svg("plus") %> New application</a>
        </div>
        <% if (apps.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("file-text") %></div><p>You haven't submitted any applications yet.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Property</th><th>Submitted</th><th>Status</th></tr></thead>
                <tbody>
                <% for (Map<String, String> a : apps) { %>
                    <tr>
                        <td><a href="PropertyServlet?action=view&propertyID=<%= a.get("propertyID") %>"><%= Html.esc(a.get("address")) %></a><div class="small muted"><%= Html.esc(a.get("type")) %></div></td>
                        <td class="nowrap"><%= a.get("date") %></td>
                        <td><span class="<%= Html.badge(a.get("status")) %>"><%= Html.esc(a.get("status")) %></span></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
