<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> leases = (List<Map<String, String>>) request.getAttribute("leases");
    if (leases == null) { response.sendRedirect("TenantServlet?action=list"); return; }
    int active = 0;
    for (Map<String, String> l : leases) { if ("Active".equals(l.get("status"))) active++; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Tenants &amp; Leases | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-tenants">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Tenants</div>
        <h1>Tenants &amp; Leases</h1>
        <p><%= leases.size() %> lease records &middot; <%= active %> active.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <div class="card">
        <div class="card-head">
            <input type="text" id="q" placeholder="Search tenant or address&hellip;" style="max-width:320px" oninput="filterRows('q','rows')">
            <div class="toolbar">
                <a href="assign_tenant.jsp" class="btn btn-outline">Assign by ID</a>
                <a href="TenantServlet?action=form" class="btn btn-primary"><%= Icon.svg("plus") %> Admit / release tenant</a>
            </div>
        </div>
        <% if (leases.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("users") %></div><p>No leases recorded yet.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Lease</th><th>Tenant</th><th>Property</th><th>Start</th><th>End</th><th>Status</th><th></th></tr></thead>
                <tbody id="rows">
                <% for (Map<String, String> l : leases) { %>
                    <tr>
                        <td><strong>#<%= l.get("tenantID") %></strong></td>
                        <td><strong><%= Html.esc(l.get("tenantName")) %></strong><div class="small muted"><%= Html.esc(l.get("email")) %></div></td>
                        <td><%= Html.esc(l.get("address")) %><div class="small muted">Property #<%= l.get("propertyID") %></div></td>
                        <td><%= l.get("leaseStart") %></td>
                        <td><%= l.get("leaseEnd").isEmpty() ? "<span class='muted'>Open-ended</span>" : l.get("leaseEnd") %></td>
                        <td><span class="badge <%= "Active".equals(l.get("status")) ? "badge-active" : "" %>"><%= l.get("status") %></span></td>
                        <td><div class="actions">
                            <a class="btn btn-sm btn-edit" href="TenantServlet?action=edit&tenantID=<%= l.get("tenantID") %>"><%= Icon.svg("pencil") %> Edit</a>
                            <a class="btn btn-sm btn-danger" href="TenantServlet?action=remove&tenantID=<%= l.get("tenantID") %>"
                               onclick="return confirm('Delete this lease record permanently?')"><%= Icon.svg("trash") %> Delete</a>
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
