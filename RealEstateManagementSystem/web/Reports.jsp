<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.dao.ReportDAO"%>
<%@page import="ac.za.tut.entities.Report"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%!
    /** Maps a saved report's type, e.g. "Tenant Report (PDF)", to the export action that regenerates it. */
    private String exportAction(String type) {
        if (type == null) return null;
        String t = type.toUpperCase();
        String fmt = t.contains("(PDF)") ? "PDF" : t.contains("(CSV)") ? "CSV" : t.contains("(TXT)") ? "TXT" : null;
        if (fmt == null) return null;
        if (t.startsWith("MAINTENANCE")) return "export" + fmt;
        if (t.startsWith("TENANT")) return "exportLeases" + fmt;
        if (t.startsWith("PROPERTY")) return "exportApps" + fmt;
        return null;
    }
%>
<%
    String role = (String) session.getAttribute("role");
    boolean canManageRecords = "Admin".equals(role) || "Manager".equals(role);
    List<Report> reports = (List<Report>) request.getAttribute("reports");
    if (reports == null) {
        try { reports = new ReportDAO().getAllReports(); } catch (Exception ex) { reports = new java.util.ArrayList<>(); }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Reports | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-reports">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Reports</div>
        <h1>Reports &amp; Compliance</h1>
        <p>Live on-screen reports with filters, plus PDF, CSV and plain-text exports for audits.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>

    <div class="tiles">
        <a class="tile" href="ReportServlet?action=summary"><span class="ico"><%= Icon.svg("trending-up") %></span><h3>Management summary</h3><p>Properties, leases, open maintenance and pending applications.</p></a>
        <a class="tile" href="ReportServlet?action=maintenance"><span class="ico"><%= Icon.svg("wrench") %></span><h3>Maintenance history</h3><p>Requests by property and tenant, filtered by status and priority.</p></a>
        <a class="tile" href="ReportServlet?action=tenantLease"><span class="ico"><%= Icon.svg("clipboard-list") %></span><h3>Tenant leases</h3><p>Tenants, their properties and lease dates.</p></a>
        <a class="tile" href="ReportServlet?action=application"><span class="ico"><%= Icon.svg("file-plus") %></span><h3>Applications</h3><p>Pending, approved and rejected applications.</p></a>
    </div>

    <div class="card" style="margin-top:28px">
        <div class="card-head"><div><h2>Quick export</h2><p>Download a full report straight away. It is logged below.</p></div></div>
        <form action="ReportServlet" method="GET" onsubmit="return route()">
            <input type="hidden" id="act" name="action" value="list">
            <div class="form-grid" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); align-items:end">
                <div class="field">
                    <label for="rt">Report</label>
                    <select id="rt">
                        <option value="Tenant">Tenant leases</option>
                        <option value="Maintenance">Maintenance</option>
                        <option value="Property">Property applications</option>
                    </select>
                </div>
                <div class="field">
                    <label for="ff">Format</label>
                    <select id="ff">
                        <option value="PDF">PDF document</option>
                        <option value="CSV">CSV spreadsheet</option>
                        <option value="TXT">Plain text</option>
                    </select>
                </div>
                <div class="field">
                    <label for="date">Report date</label>
                    <input type="date" id="date" name="date" data-today>
                </div>
                <div class="field"><button type="submit" class="btn btn-primary btn-block"><%= Icon.svg("download") %> Download</button></div>
            </div>
        </form>
    </div>

    <div class="card">
        <div class="card-head"><div><h2>Report log</h2><p><%= reports.size() %> generated report<%= reports.size() == 1 ? "" : "s" %>.</p></div></div>
        <% if (reports.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("folder-open") %></div><p>No reports yet. Export one above to see it here.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Type</th><th>By user</th><th>Date</th><th>File</th><th></th></tr></thead>
                <tbody>
                <% for (Report r : reports) { String ex = exportAction(r.getType()); %>
                    <tr>
                        <td><%= Html.esc(r.getType()) %></td>
                        <td>User <%= r.getGeneratedBy() %></td>
                        <td><%= r.getDate() %></td>
                        <td class="small muted"><%= Html.esc(r.getFilePath()) %></td>
                        <td><div class="actions">
                            <% if (ex != null) { %><a class="btn btn-sm btn-dark" href="ReportServlet?action=<%= ex %>"><%= Icon.svg("refresh") %> Regenerate</a><% } %>
                            <% if (canManageRecords) { %>
                            <a class="btn btn-sm btn-edit" href="ReportServlet?action=edit&reportID=<%= r.getReportID() %>"><%= Icon.svg("pencil") %> Edit</a>
                            <a class="btn btn-sm btn-danger" href="ReportServlet?action=delete&reportID=<%= r.getReportID() %>" onclick="return confirm('Remove this report record?')"><%= Icon.svg("trash") %> Remove</a>
                            <% } %>
                        </div></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/app.js"></script>
<script>
    function route() {
        var t = document.getElementById('rt').value, f = document.getElementById('ff').value;
        var map = { Tenant: 'exportLeases', Maintenance: 'export', Property: 'exportApps' };
        document.getElementById('act').value = map[t] + f;
        return true;
    }
</script>
</body>
</html>
