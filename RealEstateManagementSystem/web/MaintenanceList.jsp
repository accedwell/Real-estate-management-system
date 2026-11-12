<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    String role = (String) session.getAttribute("role");
    boolean canEdit = "Admin".equals(role) || "Manager".equals(role) || "Maintenance".equals(role);
    boolean canDelete = "Admin".equals(role);
    boolean canLog = !"Owner".equals(role) && !"Maintenance".equals(role);
    List<Map<String, String>> reqs = (List<Map<String, String>>) request.getAttribute("requests");
    if (reqs == null && request.getAttribute("errorMessage") == null) { response.sendRedirect("MaintenanceRequestServlet?action=list"); return; }
    int open = 0; double total = 0;
    if (reqs != null) for (Map<String, String> r : reqs) {
        if (!"Resolved".equals(r.get("status"))) open++;
        try { total += Double.parseDouble(r.get("cost")); } catch (Exception ignored) { }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Maintenance | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-maint">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Maintenance</div>
        <h1><%= "Tenant".equals(role) ? "My Maintenance Requests" : ("Maintenance".equals(role) ? "Work Orders" : "Maintenance Requests") %></h1>
        <p><%= reqs == null ? 0 : reqs.size() %> requests &middot; <strong><%= open %></strong> open &middot; R <%= String.format(java.util.Locale.ROOT, "%,.2f", total) %> total cost.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <div class="card">
        <div class="card-head">
            <div class="toolbar" style="flex:1">
                <input type="text" id="q" placeholder="Search description, tenant or address&hellip;" style="max-width:320px" oninput="filterRows('q','rows')">
            </div>
            <% if (canLog) { %><a href="MaintenanceRequestServlet?action=new" class="btn btn-primary"><%= Icon.svg("plus") %> New request</a><% } %>
        </div>
        <% if (reqs == null || reqs.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("wrench") %></div><p>No maintenance requests found.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>#</th><th>Issue</th><th>Tenant / Property</th><th>Priority</th><th>Status</th><th>Cost</th><th>Notes</th><% if (canEdit) { %><th></th><% } %></tr></thead>
                <tbody id="rows">
                <% for (Map<String, String> r : reqs) { %>
                    <tr>
                        <td><strong>#<%= r.get("requestID") %></strong></td>
                        <td style="max-width:260px"><%= Html.esc(r.get("description")) %></td>
                        <td><%= Html.esc(r.get("tenantName")) %><div class="small muted"><%= Html.esc(r.get("address")) %></div></td>
                        <td><span class="<%= Html.badge(r.get("priority")) %>"><%= Html.esc(r.get("priority")) %></span></td>
                        <td><span class="<%= Html.badge(r.get("status")) %>"><%= "InProgress".equals(r.get("status")) ? "In progress" : Html.esc(r.get("status")) %></span></td>
                        <td class="money">R <%= r.get("cost") %></td>
                        <td class="small muted" style="max-width:220px"><%= Html.escOr(r.get("resolutionNotes"), "No notes yet") %></td>
                        <% if (canEdit) { %>
                        <td><div class="actions">
                            <a class="btn btn-sm btn-edit" href="MaintenanceRequestServlet?action=edit&requestID=<%= r.get("requestID") %>"><%= Icon.svg("pencil") %> Update</a>
                            <% if (canDelete) { %>
                            <a class="btn btn-sm btn-danger" href="MaintenanceRequestServlet?action=delete&requestID=<%= r.get("requestID") %>" onclick="return confirm('Permanently delete this request?')"><%= Icon.svg("trash") %> Delete</a>
                            <% } %>
                        </div></td>
                        <% } %>
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
