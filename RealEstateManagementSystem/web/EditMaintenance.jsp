<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.model.MaintenanceRequest"%>
<%@page import="ac.za.tut.util.Html"%>
<%
    MaintenanceRequest req = (MaintenanceRequest) request.getAttribute("requestObj");
    if (req == null) { response.sendRedirect("MaintenanceRequestServlet?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Update Request | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-maint">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="MaintenanceRequestServlet?action=list">Maintenance</a> / Update</div>
        <h1>Update maintenance request</h1>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <form action="MaintenanceRequestServlet" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="requestID" value="<%= req.getRequestID() %>">
            <div class="form-grid">
                <div class="field full">
                    <label>Reported issue</label>
                    <div class="info-box"><%= Html.esc(req.getDescription()) %></div>
                </div>
                <div class="field">
                    <label for="priority">Priority</label>
                    <select id="priority" name="priority">
                        <% for (String p : new String[]{"Low", "Medium", "High"}) { %>
                            <option <%= p.equals(req.getPriority()) ? "selected" : "" %>><%= p %></option>
                        <% } %>
                    </select>
                </div>
                <div class="field">
                    <label for="status">Job status</label>
                    <select id="status" name="status">
                        <option value="Pending" <%= "Pending".equals(req.getStatus()) ? "selected" : "" %>>Pending</option>
                        <option value="InProgress" <%= "InProgress".equals(req.getStatus()) ? "selected" : "" %>>In progress</option>
                        <option value="Resolved" <%= "Resolved".equals(req.getStatus()) ? "selected" : "" %>>Resolved</option>
                    </select>
                </div>
                <div class="field">
                    <label for="cost">Cost (R)</label>
                    <input type="number" id="cost" name="cost" step="0.01" min="0" value="<%= String.format(java.util.Locale.ROOT, "%.2f", req.getCost()) %>">
                </div>
                <div class="field full">
                    <label for="resolutionNotes">Resolution notes</label>
                    <textarea id="resolutionNotes" name="resolutionNotes" rows="4" maxlength="255" placeholder="What was done, parts ordered, follow-up needed&hellip;"><%= Html.esc(req.getResolutionNotes()) %></textarea>
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="MaintenanceRequestServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Save update</button>
            </div>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
