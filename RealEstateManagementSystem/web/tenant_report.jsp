<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    List<Map<String, String>> leaseData = (List<Map<String, String>>) request.getAttribute("leaseData");
    if (leaseData == null) { response.sendRedirect("ReportServlet?action=tenantLease"); return; }
    String start = (String) request.getAttribute("filterStartDate");
    String end = (String) request.getAttribute("filterEndDate");
    if (start == null) start = "";
    if (end == null) end = "";
    String exportBase = "exportLeases";
    String exportQuery = "&startDate=" + URLEncoder.encode(start, "UTF-8") + "&endDate=" + URLEncoder.encode(end, "UTF-8");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Tenant Lease Report | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-reports">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="Reports.jsp">Reports</a> / Leases</div>
        <h1>Tenant Lease Report</h1>
        <p><%= leaseData.size() %> lease<%= leaseData.size() == 1 ? "" : "s" %><%= start.isEmpty() ? "" : " starting from " + Html.esc(start) %><%= end.isEmpty() ? "" : " and ending by " + Html.esc(end) %>.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <form action="ReportServlet" method="GET" class="form-grid" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); align-items:end; margin-bottom:22px">
            <input type="hidden" name="action" value="tenantLease">
            <div class="field"><label for="startDate">Lease starts on/after</label><input type="date" id="startDate" name="startDate" value="<%= Html.esc(start) %>"></div>
            <div class="field"><label for="endDate">Lease ends on/before</label><input type="date" id="endDate" name="endDate" value="<%= Html.esc(end) %>"></div>
            <div class="field" style="display:flex; gap:8px">
                <button type="submit" class="btn btn-dark"><%= Icon.svg("filter") %> Filter</button>
                <a class="btn btn-outline" href="ReportServlet?action=tenantLease">Clear</a>
            </div>
        </form>
        <p class="hint" style="margin:-10px 0 18px">Both dates are needed for the filter to apply.</p>

        <div class="card-head"><h2>Leases</h2><%@ include file="/WEB-INF/jspf/exportButtons.jspf" %></div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Tenant</th><th>Property</th><th>Lease start</th><th>Lease end</th></tr></thead>
                <tbody>
                <% if (leaseData.isEmpty()) { %>
                    <tr><td colspan="4" class="empty">No leases found for the selected dates.</td></tr>
                <% } else for (Map<String, String> row : leaseData) { %>
                    <tr>
                        <td><strong><%= Html.esc(row.get("tenantName")) %></strong></td>
                        <td><%= Html.esc(row.get("address")) %></td>
                        <td><%= Html.esc(row.get("leaseStart")) %></td>
                        <td><%= Html.escOr(row.get("leaseEnd"), "Open-ended") %></td>
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
