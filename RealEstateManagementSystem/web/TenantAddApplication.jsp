<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.dao.PropertyDAO"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    List<Property> all = new PropertyDAO().getAllProperties();
    String chosen = request.getParameter("propertyID");
    int availableCount = 0;
    for (Property p : all) { if ("Available".equals(p.getStatus())) availableCount++; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Apply for a Property | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-apps">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Apply</div>
        <h1>Apply for a property</h1>
        <p>Your application goes straight to the property manager and starts as <strong>Pending</strong>.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <% if (availableCount == 0) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("house") %></div><p>There are no available properties right now. Please check back soon.</p></div>
        <% } else { %>
        <form action="ApplicationServlet" method="POST">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="status" value="Pending">
            <div class="form-grid">
                <div class="field full">
                    <label>Applicant</label>
                    <input type="text" value="<%= Html.esc(session.getAttribute("name")) %> (User #<%= session.getAttribute("userID") %>)" readonly>
                </div>
                <div class="field full">
                    <label for="propertyID">Property you are applying for</label>
                    <select id="propertyID" name="propertyID" required>
                        <option value="" disabled <%= chosen == null ? "selected" : "" %>>Choose an available property&hellip;</option>
                        <% for (Property p : all) { if (!"Available".equals(p.getStatus())) continue; %>
                            <option value="<%= p.getPropertyID() %>" <%= String.valueOf(p.getPropertyID()).equals(chosen) ? "selected" : "" %>>
                                <%= Html.esc(p.getAddress()) %> &middot; <%= Html.esc(p.getType()) %>, <%= p.getRooms() %> room<%= p.getRooms() == 1 ? "" : "s" %>, <%= p.getSize() %> m&sup2;
                            </option>
                        <% } %>
                    </select>
                </div>
                <div class="field">
                    <label for="date">Application date</label>
                    <input type="date" id="date" name="date" required data-today>
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="Welcome.jsp">Cancel</a>
                <button type="submit" class="btn btn-primary">Submit application</button>
            </div>
        </form>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
</body>
</html>
