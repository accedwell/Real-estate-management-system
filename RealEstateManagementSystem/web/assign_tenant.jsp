<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Assign Lease | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-tenants">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="TenantServlet?action=list">Tenants</a> / Assign by ID</div>
        <h1>Assign a lease to a tenant</h1>
        <p>Quick entry by ID. Every field is validated (required fields, numbers, dates and length) before the lease is saved.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="AssignTenantServlet" method="POST" novalidate>
            <div class="form-grid">
                <div class="field">
                    <label for="tenantID">Tenant user ID</label>
                    <input type="text" id="tenantID" name="tenantID" placeholder="e.g. 2" value="<%= Html.esc(request.getParameter("tenantID")) %>">
                </div>
                <div class="field">
                    <label for="propertyID">Property ID</label>
                    <input type="text" id="propertyID" name="propertyID" placeholder="e.g. 1" value="<%= Html.esc(request.getParameter("propertyID")) %>">
                </div>
                <div class="field">
                    <label for="leaseStart">Lease start date</label>
                    <input type="date" id="leaseStart" name="leaseStart" value="<%= Html.esc(request.getParameter("leaseStart")) %>">
                </div>
                <div class="field">
                    <label for="leaseEnd">Lease end date</label>
                    <input type="date" id="leaseEnd" name="leaseEnd" value="<%= Html.esc(request.getParameter("leaseEnd")) %>">
                </div>
                <div class="field full">
                    <label for="notes">Lease terms / notes <span class="muted">(max 100 characters)</span></label>
                    <textarea id="notes" name="notes" rows="3" placeholder="Special conditions&hellip;"><%= Html.esc(request.getParameter("notes")) %></textarea>
                    <p class="hint"><span id="count">0</span>/100 characters</p>
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="TenantServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Save assignment</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script>
    var n = document.getElementById('notes'), c = document.getElementById('count');
    function upd() { c.textContent = n.value.length; c.style.color = n.value.length > 100 ? '#b91c1c' : ''; }
    n.addEventListener('input', upd); upd();
</script>
</body>
</html>
