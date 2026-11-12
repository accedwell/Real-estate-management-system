<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> apps = (List<Map<String, String>>) request.getAttribute("applications");
    if (apps == null) { response.sendRedirect("ApplicationServlet?action=list"); return; }
    int pending = 0;
    for (Map<String, String> a : apps) { if ("Pending".equals(a.get("status"))) pending++; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Rental Applications | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-apps">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Applications</div>
        <h1>Rental Applications</h1>
        <p><%= apps.size() %> applications &middot; <strong><%= pending %></strong> waiting for a decision.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <div class="card">
        <div class="card-head">
            <input type="text" id="q" placeholder="Search applicant, address or status&hellip;" style="max-width:320px" oninput="filterRows('q','rows')">
            <a href="ApplicationServlet?action=new" class="btn btn-primary"><%= Icon.svg("plus") %> New application</a>
        </div>
        <% if (apps.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("file-text") %></div><p>No applications yet.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Applicant</th><th>Property</th><th>Date</th><th>Status</th><th></th></tr></thead>
                <tbody id="rows">
                <% for (Map<String, String> a : apps) { String id = a.get("applicationID"); %>
                    <tr>
                        <td><%= Html.esc(a.get("applicantName")) %></td>
                        <td><%= Html.esc(a.get("address")) %><div class="small muted"><%= Html.esc(a.get("type")) %></div></td>
                        <td class="nowrap"><%= a.get("date") %></td>
                        <td><span class="<%= Html.badge(a.get("status")) %>"><%= Html.esc(a.get("status")) %></span></td>
                        <td><div class="actions">
                            <% if ("Pending".equals(a.get("status"))) { %>
                                <a class="btn btn-sm btn-approve" href="ApplicationServlet?action=approve&applicationID=<%= id %>"><%= Icon.svg("check") %> Approve</a>
                                <a class="btn btn-sm btn-reject" href="ApplicationServlet?action=reject&applicationID=<%= id %>"><%= Icon.svg("x") %> Reject</a>
                            <% } else if ("Approved".equals(a.get("status"))) { %>
                                <a class="btn btn-sm btn-dark" href="TenantServlet?action=form&userID=<%= a.get("tenantID") %>&propertyID=<%= a.get("propertyID") %>"><%= Icon.svg("door-open") %> Admit tenant</a>
                            <% } %>
                            <a class="btn btn-sm btn-edit" href="ApplicationServlet?action=edit&applicationID=<%= id %>"><%= Icon.svg("pencil") %> Edit</a>
                            <a class="btn btn-sm btn-outline" href="ApplicationServlet?action=delete&applicationID=<%= id %>" onclick="return confirm('Delete this application?')"><%= Icon.svg("trash") %> Delete</a>
                        </div></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
</body>
</html>
