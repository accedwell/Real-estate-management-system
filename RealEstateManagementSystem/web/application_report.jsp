<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> appData = (List<Map<String, String>>) request.getAttribute("appData");
    if (appData == null) { response.sendRedirect("ReportServlet?action=application"); return; }
    String currentStatus = (String) request.getAttribute("filterStatus");
    if (currentStatus == null) currentStatus = "ALL";
    String exportBase = "exportApps";
    String exportQuery = "&status=" + URLEncoder.encode(currentStatus, "UTF-8");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Applications Report | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-reports">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="Reports.jsp">Reports</a> / Applications</div>
        <h1>Property Applications Report</h1>
        <p><%= appData.size() %> application<%= appData.size() == 1 ? "" : "s" %> &middot; status: <%= "ALL".equals(currentStatus) ? "all" : Html.esc(currentStatus) %>.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <form action="ReportServlet" method="GET" class="form-grid" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); align-items:end; margin-bottom:22px">
            <input type="hidden" name="action" value="application">
            <div class="field">
                <label for="status">Status</label>
                <select id="status" name="status">
                    <% for (String s : new String[]{"ALL", "Pending", "Approved", "Rejected"}) { %>
                        <option value="<%= s %>" <%= s.equalsIgnoreCase(currentStatus) ? "selected" : "" %>><%= "ALL".equals(s) ? "All statuses" : s %></option>
                    <% } %>
                </select>
            </div>
            <div class="field" style="display:flex; gap:8px">
                <button type="submit" class="btn btn-dark"><%= Icon.svg("filter") %> Filter</button>
                <a class="btn btn-outline" href="ReportServlet?action=application">Clear</a>
            </div>
        </form>

        <div class="card-head"><h2>Applications</h2><%@ include file="/WEB-INF/jspf/exportButtons.jspf" %></div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Applicant</th><th>Property</th><th>Date</th><th>Status</th></tr></thead>
                <tbody>
                <% if (appData.isEmpty()) { %>
                    <tr><td colspan="4" class="empty">No applications match this filter.</td></tr>
                <% } else for (Map<String, String> row : appData) { %>
                    <tr>
                        <td><strong><%= Html.esc(row.get("applicantName")) %></strong></td>
                        <td><%= Html.esc(row.get("address")) %></td>
                        <td><%= Html.esc(row.get("date")) %></td>
                        <td><span class="<%= Html.badge(row.get("status")) %>"><%= Html.esc(row.get("status")) %></span></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="form-actions"><a class="btn btn-outline" href="Reports.jsp"><%= Icon.svg("arrow-left") %> Back to reports</a></div>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
