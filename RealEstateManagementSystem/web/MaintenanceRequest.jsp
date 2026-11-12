<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> leases = (List<Map<String, String>>) request.getAttribute("leases");
    if (leases == null) { response.sendRedirect("MaintenanceRequestServlet?action=new"); return; }
    boolean isTenant = "Tenant".equals(session.getAttribute("role"));
    String chosen = request.getParameter("tenantID");
    String chosenPriority = request.getParameter("priority");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>New Maintenance Request | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-maint">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="MaintenanceRequestServlet?action=list">Maintenance</a> / New</div>
        <h1>Report a maintenance issue</h1>
        <p>Describe the problem and how urgent it is. The maintenance team will update you as work progresses.</p>
    </div>
</section>

<main class="page page-narrow">
    <div class="card">
        <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
        <% if (leases.isEmpty()) { %>
            <div class="empty">
                <div class="icon"><%= Icon.svg("key") %></div>
                <p><%= isTenant ? "You don't have a lease yet, so there is no property to log a request against."
                                : "There are no leases yet. Admit a tenant first, then log requests against their lease." %></p>
            </div>
        <% } else { %>
        <form action="MaintenanceRequestServlet" method="POST">
            <input type="hidden" name="action" value="add">
            <div class="form-grid">
                <div class="field full">
                    <label for="tenantID"><%= isTenant ? "Your property" : "Tenant and property (lease)" %></label>
                    <select id="tenantID" name="tenantID" required>
                        <% if (leases.size() > 1) { %><option value="" disabled <%= chosen == null ? "selected" : "" %>>Choose&hellip;</option><% } %>
                        <% for (Map<String, String> l : leases) { %>
                            <option value="<%= l.get("tenantID") %>" <%= l.get("tenantID").equals(chosen) ? "selected" : "" %>>
                                <%= isTenant ? "" : Html.esc(l.get("tenantName")) + " &middot; " %><%= Html.esc(l.get("address")) %><%= "Ended".equals(l.get("status")) ? " (lease ended)" : "" %>
                            </option>
                        <% } %>
                    </select>
                </div>
                <div class="field full">
                    <label for="description">What's the problem?</label>
                    <textarea id="description" name="description" rows="4" maxlength="255" required placeholder="e.g. The kitchen tap is leaking under the sink."><%= Html.esc(request.getParameter("description")) %></textarea>
                </div>
                <div class="field">
                    <label for="priority">Priority</label>
                    <select id="priority" name="priority" required>
                        <option value="Low" <%= "Low".equals(chosenPriority) ? "selected" : "" %>>Low &mdash; not urgent</option>
                        <option value="Medium" <%= chosenPriority == null || "Medium".equals(chosenPriority) ? "selected" : "" %>>Medium &mdash; normal</option>
                        <option value="High" <%= "High".equals(chosenPriority) ? "selected" : "" %>>High &mdash; emergency</option>
                    </select>
                </div>
                <div class="field">
                    <label>Status</label>
                    <input type="text" value="Pending" readonly>
                </div>
                <div class="field full">
                    <label for="resolutionNotes">Extra notes <span class="muted">(optional &mdash; access times, contact details)</span></label>
                    <textarea id="resolutionNotes" name="resolutionNotes" rows="2" maxlength="255"><%= Html.esc(request.getParameter("resolutionNotes")) %></textarea>
                </div>
            </div>
            <div class="form-actions">
                <a class="btn btn-outline" href="MaintenanceRequestServlet?action=list">Cancel</a>
                <button type="submit" class="btn btn-dark">Submit request</button>
            </div>
        </form>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
