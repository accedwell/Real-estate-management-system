<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.util.Html"%>
<%
    String role = (String) session.getAttribute("role");
    boolean canManage = "Admin".equals(role) || "Manager".equals(role) || "Agent".equals(role);
    Property p = (Property) request.getAttribute("property");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Property Details | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-props">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="PropertyServlet?action=list">Properties</a> / Details</div>
        <h1><%= p != null ? Html.esc(p.getAddress()) : "Property not found" %></h1>
        <% if (p != null) { %><p><%= Html.esc(p.getType()) %></p><% } %>
    </div>
</section>

<main class="page page-narrow">
    <% if (p == null) { %>
        <div class="card"><div class="empty"><div class="icon"><%= Icon.svg("alert") %></div><p>No details are available for that property.</p>
            <a class="btn btn-dark" href="PropertyServlet?action=list">Back to listings</a></div></div>
    <% } else { %>
    <div class="card" style="padding:0; overflow:hidden">
        <div style="height:280px; background:url('<%= Html.propertyImage(p.getType(), p.getPropertyID()) %>') center/cover"></div>
        <div style="padding:28px">
            <div class="card-head">
                <h2>Specification</h2>
                <span class="<%= Html.badge(p.getStatus()) %>"><%= Html.esc(p.getStatus()) %></span>
            </div>
            <div class="stats">
                <div class="stat"><h3>Type</h3><div class="num" style="font-size:1.5rem"><%= Html.esc(p.getType()) %></div></div>
                <div class="stat"><h3>Floor area</h3><div class="num" style="font-size:1.5rem"><%= p.getSize() %> m&sup2;</div></div>
                <div class="stat"><h3>Rooms</h3><div class="num" style="font-size:1.5rem"><%= p.getRooms() %></div></div>
            </div>
            <table class="data" style="margin-top:24px">
                <tr><td class="muted">Address</td><td><strong><%= Html.esc(p.getAddress()) %></strong></td></tr>
                <tr><td class="muted">Owner</td><td><%= Html.escOr(p.getOwnerName(), "User " + p.getOwnerID()) %></td></tr>
            </table>
            <div class="form-actions">
                <a class="btn btn-outline" href="PropertyServlet?action=list"><%= Icon.svg("arrow-left") %> Back</a>
                <% if (canManage) { %>
                    <a class="btn btn-dark" href="PropertyServlet?action=edit&propertyID=<%= p.getPropertyID() %>"><%= Icon.svg("pencil") %> Edit property</a>
                <% } else if ("Tenant".equals(role) && "Available".equals(p.getStatus())) { %>
                    <a class="btn btn-primary" href="TenantAddApplication.jsp?propertyID=<%= p.getPropertyID() %>"><%= Icon.svg("file-plus") %> Apply for this property</a>
                <% } %>
            </div>
        </div>
    </div>
    <% } %>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
