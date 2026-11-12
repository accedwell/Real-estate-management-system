<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    List<Property> mine = (List<Property>) request.getAttribute("myProperties");
    if (mine == null) { response.sendRedirect("PropertyListServlet"); return; }
    String title = (String) request.getAttribute("myTitle");
    boolean isTenant = "Tenant".equals(session.getAttribute("role"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title><%= Html.escOr(title, "My Properties") %> | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-props">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <%= Html.escOr(title, "My Properties") %></div>
        <h1><%= Html.escOr(title, "My Properties") %></h1>
        <p><%= mine.size() %> propert<%= mine.size() == 1 ? "y" : "ies" %> linked to your account.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <% if (mine.isEmpty()) { %>
            <div class="empty">
                <div class="icon"><%= Icon.svg("key") %></div>
                <% if (isTenant) { %>
                    <p>You are not leasing any property yet.</p>
                    <a class="btn btn-primary" href="PropertyServlet?action=list" style="margin-top:12px">Browse available homes</a>
                <% } else { %>
                    <p>No properties are registered in your name yet. An agent or manager can add them for you.</p>
                <% } %>
            </div>
        <% } else { %>
        <div class="prop-grid">
            <% for (Property p : mine) { %>
            <a class="prop-card" href="PropertyServlet?action=view&propertyID=<%= p.getPropertyID() %>" style="color:inherit">
                <div class="ph" style="background-image:url('<%= Html.propertyImage(p.getType(), p.getPropertyID()) %>')">
                    <span class="<%= Html.badge(p.getStatus()) %>"><%= Html.esc(p.getStatus()) %></span>
                </div>
                <div class="body">
                    <h3><%= Html.esc(p.getAddress()) %></h3>
                    <div class="meta"><span><%= Icon.svg("house") %> <%= Html.esc(p.getType()) %></span><span><%= Icon.svg("ruler") %> <%= p.getSize() %> m&sup2;</span><span><%= Icon.svg("bed") %> <%= p.getRooms() %> room<%= p.getRooms() == 1 ? "" : "s" %></span></div>
                </div>
            </a>
            <% } %>
        </div>
        <% } %>
        <% if (isTenant && !mine.isEmpty()) { %>
            <div class="form-actions"><a class="btn btn-dark" href="MaintenanceRequestServlet?action=new">Report a maintenance issue</a></div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
