<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> reportData = (List<Map<String, String>>) request.getAttribute("reportData");
    if (reportData == null) { response.sendRedirect("ReportServlet?action=maintenance"); return; }
    String currentStatus = (String) request.getAttribute("filterStatus");
    String currentPriority = (String) request.getAttribute("filterPriority");
    if (currentStatus == null) currentStatus = "ALL";
    if (currentPriority == null) currentPriority = "ALL";
    double total = 0;
    for (Map<String, String> r : reportData) { try { total += Double.parseDouble(r.get("cost")); } catch (Exception ignored) { } }
    String exportBase = "export";
    String exportQuery = "&status=" + URLEncoder.encode(currentStatus, "UTF-8") + "&priority=" + URLEncoder.encode(currentPriority, "UTF-8");
    boolean isOwner = "Owner".equals(session.getAttribute("role"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Maintenance Report | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-maint">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <% if (!isOwner) { %><a href="Reports.jsp">Reports</a> / <% } %>Maintenance</div>
        <h1>Maintenance Report</h1>
        <p><%= isOwner ? "Requests on properties you own. " : "" %><%= reportData.size() %> request<%= reportData.size() == 1 ? "" : "s" %> &middot; total cost R <%= String.format(java.util.Locale.ROOT, "%,.2f", total) %>.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <form action="ReportServlet" method="GET" class="form-grid" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); align-items:end; margin-bottom:22px">
            <input type="hidden" name="action" value="maintenance">
            <div class="field">
                <label for="status">Status</label>
                <select id="status" name="status">
                    <% for (String[] s : new String[][]{{"ALL","All statuses"},{"Pending","Pending"},{"InProgress","In progress"},{"Resolved","Resolved"}}) { %>
                        <option value="<%= s[0] %>" <%= s[0].equalsIgnoreCase(currentStatus) ? "selected" : "" %>><%= s[1] %></option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="priority">Priority</label>
                <select id="priority" name="priority">
                    <% for (String[] s : new String[][]{{"ALL","All priorities"},{"Low","Low"},{"Medium","Medium"},{"High","High"}}) { %>
                        <option value="<%= s[0] %>" <%= s[0].equalsIgnoreCase(currentPriority) ? "selected" : "" %>><%= s[1] %></option>
                    <% } %>
                </select>
            </div>
            <div class="field" style="display:flex; gap:8px">
                <button type="submit" class="btn btn-dark"><%= Icon.svg("filter") %> Filter</button>
                <a class="btn btn-outline" href="ReportServlet?action=maintenance">Clear</a>
            </div>
        </form>

        <div class="card-head"><h2>Requests</h2><%@ include file="/WEB-INF/jspf/exportButtons.jspf" %></div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Property</th><th>Tenant</th><th>Issue</th><th>Priority</th><th>Cost</th><th>Status</th></tr></thead>
                <tbody>
                <% if (reportData.isEmpty()) { %>
                    <tr><td colspan="6" class="empty">No maintenance requests match these filters.</td></tr>
                <% } else for (Map<String, String> row : reportData) { %>
                    <tr>
                        <td><%= Html.esc(row.get("address")) %></td>
                        <td><strong><%= Html.esc(row.get("tenantName")) %></strong></td>
                        <td style="max-width:280px"><%= Html.esc(row.get("description")) %></td>
                        <td><span class="<%= Html.badge(row.get("priority")) %>"><%= Html.esc(row.get("priority")) %></span></td>
                        <td class="money">R <%= Html.esc(row.get("cost")) %></td>
                        <td><span class="<%= Html.badge(row.get("status")) %>"><%= "InProgress".equals(row.get("status")) ? "In progress" : Html.esc(row.get("status")) %></span></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="form-actions"><a class="btn btn-outline" href="<%= isOwner ? "Welcome.jsp" : "Reports.jsp" %>"><%= Icon.svg("arrow-left") %> Back</a></div>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
