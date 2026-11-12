<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.entities.Tenant"%>
<%@page import="ac.za.tut.entities.User"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    Tenant t = (Tenant) request.getAttribute("tenantObj");
    List<User> users = (List<User>) request.getAttribute("users");
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (t == null || users == null || properties == null) { response.sendRedirect("TenantServlet?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Edit Lease | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-tenants">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="TenantServlet?action=list">Tenants</a> / Edit</div>
        <h1>Edit lease</h1>
        <p>Change the tenant, property or lease dates.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="TenantServlet" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="tenantID" value="<%= t.getTenantID() %>">
            <div class="form-grid">
                <div class="field">
                    <label for="userID">Tenant</label>
                    <select id="userID" name="userID" required>
                        <% for (User u : users) { %>
                            <option value="<%= u.getUserID() %>" <%= u.getUserID() == t.getUserID() ? "selected" : "" %>><%= Html.esc(u.getName()) %> (ID <%= u.getUserID() %>)</option>
                        <% } %>
                    </select>
                </div>
                <div class="field">
                    <label for="propertyID">Property</label>
                    <select id="propertyID" name="propertyID" required>
                        <% for (Property p : properties) { %>
                            <option value="<%= p.getPropertyID() %>" <%= p.getPropertyID() == t.getPropertyID() ? "selected" : "" %>><%= Html.esc(p.getAddress()) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="field">
                    <label for="leaseStart">Lease start</label>
                    <input type="date" id="leaseStart" name="leaseStart" required value="<%= t.getLeaseStart() != null ? t.getLeaseStart() : "" %>">
                </div>
                <div class="field">
                    <label for="leaseEnd">Lease end <span class="muted">(optional)</span></label>
                    <input type="date" id="leaseEnd" name="leaseEnd" value="<%= t.getLeaseEnd() != null ? t.getLeaseEnd() : "" %>">
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="TenantServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Save changes</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
