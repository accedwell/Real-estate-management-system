<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.dao.PropertyDAO"%>
<%@page import="ac.za.tut.dao.UserDAO"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.entities.User"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (users == null) { users = new UserDAO().getUsersByRole("Tenant"); }
    if (properties == null) { properties = new PropertyDAO().getAllProperties(); }
    String selUser = request.getParameter("userID");
    String selProp = request.getParameter("propertyID");
    boolean leaving = "leave".equals(request.getParameter("action"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Admit or Release Tenant | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-tenants">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="TenantServlet?action=list">Tenants</a> / Admission</div>
        <h1>Admit or release a tenant</h1>
        <p>Admitting creates a lease and marks the property as Rented. A departure closes the lease today and makes the property Available.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="TenantServlet" method="POST" id="tf">
            <div class="form-grid">
                <div class="field full">
                    <label>What would you like to do?</label>
                    <select name="action" id="act" onchange="toggleDates()">
                        <option value="admit" <%= leaving ? "" : "selected" %>>Admit tenant (start a lease)</option>
                        <option value="leave" <%= leaving ? "selected" : "" %>>Tenant is leaving (end the lease)</option>
                    </select>
                </div>
                <div class="field">
                    <label for="userID">Tenant</label>
                    <select id="userID" name="userID" required>
                        <option value="" disabled <%= selUser == null ? "selected" : "" %>>Choose a tenant&hellip;</option>
                        <% for (User u : users) { %>
                            <option value="<%= u.getUserID() %>" <%= String.valueOf(u.getUserID()).equals(selUser) ? "selected" : "" %>><%= Html.esc(u.getName()) %> (ID <%= u.getUserID() %>)</option>
                        <% } %>
                    </select>
                    <% if (users.isEmpty()) { %><p class="hint">No Tenant accounts exist yet.</p><% } %>
                </div>
                <div class="field">
                    <label for="propertyID">Property</label>
                    <select id="propertyID" name="propertyID" required>
                        <option value="" disabled <%= selProp == null ? "selected" : "" %>>Choose a property&hellip;</option>
                        <% for (Property p : properties) { %>
                            <option value="<%= p.getPropertyID() %>" <%= String.valueOf(p.getPropertyID()).equals(selProp) ? "selected" : "" %>><%= Html.esc(p.getAddress()) %> &middot; <%= Html.esc(p.getStatus()) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="field dates">
                    <label for="leaseStart">Lease start</label>
                    <input type="date" id="leaseStart" name="leaseStart" data-today value="<%= Html.esc(request.getParameter("leaseStart")) %>">
                </div>
                <div class="field dates">
                    <label for="leaseEnd">Lease end <span class="muted">(optional)</span></label>
                    <input type="date" id="leaseEnd" name="leaseEnd" value="<%= Html.esc(request.getParameter("leaseEnd")) %>">
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="TenantServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark" id="go">Admit tenant</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
<script>
    function toggleDates() {
        var leave = document.getElementById('act').value === 'leave';
        document.querySelectorAll('.dates').forEach(function (d) { d.style.display = leave ? 'none' : ''; });
        document.getElementById('leaseStart').required = !leave;
        document.getElementById('go').textContent = leave ? 'Record departure' : 'Admit tenant';
    }
    toggleDates();
</script>
</body>
</html>
