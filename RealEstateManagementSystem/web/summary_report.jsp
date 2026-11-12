<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%
    Map<String, Integer> metrics = (Map<String, Integer>) request.getAttribute("metrics");
    if (metrics == null) { response.sendRedirect("ReportServlet?action=summary"); return; }
    int totalProperties = metrics.getOrDefault("totalProperties", 0);
    int activeLeases = metrics.getOrDefault("activeLeases", 0);
    int openMaintenance = metrics.getOrDefault("openMaintenance", 0);
    int pendingApps = metrics.getOrDefault("pendingApps", 0);
    String exportBase = "exportSummary";
    String exportQuery = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Management Summary | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-reports">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / <a href="Reports.jsp">Reports</a> / Summary</div>
        <h1>Management Summary</h1>
        <p>Live totals across the whole system, as at <%= java.time.LocalDate.now() %>.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <div class="card-head"><h2>Key figures</h2><%@ include file="/WEB-INF/jspf/exportButtons.jspf" %></div>
        <div class="stats">
            <div class="stat"><h3>Total properties</h3><div class="num"><%= totalProperties %></div><p>Registered listings</p></div>
            <div class="stat" style="border-top-color:#15803d"><h3>Lease records</h3><div class="num"><%= activeLeases %></div><p>Tenant leases on file</p></div>
            <div class="stat" style="border-top-color:#b91c1c"><h3>Open maintenance</h3><div class="num"><%= openMaintenance %></div><p>Pending or in progress</p></div>
            <div class="stat" style="border-top-color:#1d4ed8"><h3>Pending applications</h3><div class="num"><%= pendingApps %></div><p>Awaiting approval</p></div>
        </div>
        <div class="form-actions"><a class="btn btn-outline" href="Reports.jsp"><%= Icon.svg("arrow-left") %> Back to reports</a></div>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
