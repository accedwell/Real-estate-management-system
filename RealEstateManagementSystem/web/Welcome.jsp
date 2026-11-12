<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="ac.za.tut.util.Icon"%>
<%@page import="ac.za.tut.dao.ReportDAO"%>
<%@page import="ac.za.tut.dao.PropertyDAO"%>
<%@page import="ac.za.tut.dao.TenantDAO"%>
<%@page import="ac.za.tut.dao.ApplicationDAO"%>
<%@page import="ac.za.tut.dao.MaintenanceRequestDAO"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="java.util.*"%>
<%!
    /** One KPI card. tone = gold | green | red | blue */
    private String kpi(String icon, String tone, String label, Object value, String note) {
        return "<div class=\"kpi kpi-" + tone + "\"><span class=\"kpi-ico\">" + Icon.svg(icon) + "</span>"
                + "<div><p class=\"kpi-label\">" + label + "</p><p class=\"kpi-num\">" + value + "</p>"
                + "<p class=\"kpi-note\">" + note + "</p></div></div>";
    }

    /** One quick-action row. */
    private String qa(String href, String icon, String label) {
        return "<a class=\"qa\" href=\"" + href + "\"><span class=\"qa-ico\">" + Icon.svg(icon) + "</span><span>"
                + label + "</span>" + Icon.svg("arrow-right", "i qa-go") + "</a>";
    }

    /** One labelled progress bar. */
    private String bar(String label, int value, int total, String tone) {
        int pct = total == 0 ? 0 : Math.round(value * 100f / total);
        return "<div class=\"bar-row\"><div class=\"bar-top\"><span>" + label + "</span><strong>" + value
                + " <em>" + pct + "%</em></strong></div><div class=\"bar\"><span class=\"bar-" + tone
                + "\" style=\"width:" + pct + "%\"></span></div></div>";
    }

    private static <T> List<T> top(List<T> list, int n) {
        return list == null ? new ArrayList<T>() : list.subList(0, Math.min(n, list.size()));
    }
%>
<%
    String role = (String) session.getAttribute("role");
    String name = (String) session.getAttribute("name");
    int userID = (Integer) session.getAttribute("userID");

    boolean isAdmin = "Admin".equals(role), isManager = "Manager".equals(role), isAgent = "Agent".equals(role);
    boolean isStaff = isAdmin || isManager || isAgent;
    boolean isTenant = "Tenant".equals(role), isOwner = "Owner".equals(role), isMaint = "Maintenance".equals(role);

    int hour = java.time.LocalTime.now().getHour();
    String greeting = hour < 12 ? "Good morning" : (hour < 17 ? "Good afternoon" : "Good evening");
    String first = (name == null || name.trim().isEmpty()) ? "there" : name.trim();

    // ---------------------------------------------------------------- data for this role
    String dataError = null;
    Map<String, Integer> m = new HashMap<>();
    List<Property> props = new ArrayList<>();
    List<Map<String, String>> requests = new ArrayList<>();
    List<Map<String, String>> apps = new ArrayList<>();
    List<Map<String, String>> leases = new ArrayList<>();
    try {
        PropertyDAO pdao = new PropertyDAO();
        if (isStaff) {
            m = new ReportDAO().getSummaryMetrics();
            props = pdao.getAllProperties();
            requests = new MaintenanceRequestDAO().getRequestDetails(null);
            apps = new ApplicationDAO().getApplicationDetails(null);
        } else if (isMaint) {
            requests = new MaintenanceRequestDAO().getRequestDetails(null);
        } else if (isTenant) {
            props = pdao.getAllProperties();
            leases = new TenantDAO().getLeaseDetails(userID);
            requests = new MaintenanceRequestDAO().getRequestDetails(userID);
            apps = new ApplicationDAO().getApplicationDetails(userID);
        } else if (isOwner) {
            props = pdao.getPropertiesByOwner(userID);
            requests = new ReportDAO().getMaintenanceReport(userID, "Owner", "ALL", "ALL");
        }
    } catch (Exception e) {
        dataError = "Some dashboard data could not be loaded. Check that the database is running.";
    }

    int available = 0, rented = 0, sold = 0;
    for (Property p : props) {
        if ("Available".equals(p.getStatus())) available++;
        else if ("Rented".equals(p.getStatus())) rented++;
        else if ("Sold".equals(p.getStatus())) sold++;
    }
    int open = 0, inProgress = 0, pending = 0, resolved = 0, high = 0, medium = 0, low = 0;
    double spend = 0;
    for (Map<String, String> r : requests) {
        String st = r.get("status");
        if ("Pending".equals(st)) pending++;
        else if ("InProgress".equals(st)) inProgress++;
        else if ("Resolved".equals(st)) resolved++;
        if (!"Resolved".equals(st)) {
            open++;
            if ("High".equals(r.get("priority"))) high++;
            else if ("Medium".equals(r.get("priority"))) medium++;
            else low++;
        }
        try { spend += Double.parseDouble(r.get("cost")); } catch (Exception ignored) { }
    }
    int appsPending = 0;
    for (Map<String, String> a : apps) if ("Pending".equals(a.get("status"))) appsPending++;
    int activeLeases = 0;
    for (Map<String, String> l : leases) if ("Active".equals(l.get("status"))) activeLeases++;
    String money = "R " + String.format(Locale.ROOT, "%,.0f", spend).replace(',', ' ');
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Dashboard | REMS</title>
</head>
<body class="app">
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<div class="dash">
    <section class="dash-hero">
        <div>
            <p class="dash-kicker"><%= navRoleLabel %> dashboard</p>
            <h1><%= greeting %>, <%= Html.esc(first) %></h1>
            <p class="dash-sub">
                <% if (isStaff) { %>Here's what's happening across your properties today.
                <% } else if (isMaint) { %>You have <strong><%= open %></strong> open work order<%= open == 1 ? "" : "s" %><%= high > 0 ? ", " + high + " marked high priority" : "" %>.
                <% } else if (isTenant) { %>Everything about your home, applications and repairs in one place.
                <% } else { %>An overview of your portfolio and the maintenance on it.<% } %>
            </p>
        </div>
        <div class="dash-hero-actions">
            <% if (isStaff) { %>
                <a class="btn btn-primary" href="PropertyServlet?action=add"><%= Icon.svg("plus") %> Add property</a>
                <a class="btn btn-ghost" href="TenantServlet?action=form"><%= Icon.svg("door-open") %> Admit tenant</a>
            <% } else if (isTenant) { %>
                <a class="btn btn-primary" href="TenantAddApplication.jsp"><%= Icon.svg("file-plus") %> Apply for a property</a>
                <a class="btn btn-ghost" href="MaintenanceRequestServlet?action=new"><%= Icon.svg("wrench") %> Report an issue</a>
            <% } else if (isMaint) { %>
                <a class="btn btn-primary" href="MaintenanceRequestServlet?action=list"><%= Icon.svg("clipboard-list") %> Open work orders</a>
            <% } else { %>
                <a class="btn btn-primary" href="PropertyListServlet"><%= Icon.svg("key") %> My properties</a>
                <a class="btn btn-ghost" href="ReportServlet?action=maintenance"><%= Icon.svg("download") %> Maintenance report</a>
            <% } %>
        </div>
    </section>

    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>
    <% if (dataError != null) { %><div class="alert alert-error"><%= Icon.svg("alert") %><span><%= dataError %></span></div><% } %>

    <!-- ============================== KPIs ============================== -->
    <div class="kpis">
        <% if (isStaff) { %>
            <%= kpi("house", "gold", "Properties", m.getOrDefault("totalProperties", 0), available + " available &middot; " + rented + " rented") %>
            <%= kpi("users", "green", "Lease records", m.getOrDefault("activeLeases", 0), "Tenants on file") %>
            <%= kpi("wrench", "red", "Open maintenance", m.getOrDefault("openMaintenance", 0), high + " high priority") %>
            <%= kpi("file-text", "blue", "Pending applications", m.getOrDefault("pendingApps", 0), "Awaiting a decision") %>
        <% } else if (isMaint) { %>
            <%= kpi("clipboard-list", "gold", "Pending", pending, "Not started yet") %>
            <%= kpi("clock", "blue", "In progress", inProgress, "Currently being worked on") %>
            <%= kpi("alert", "red", "High priority", high, "Open emergencies") %>
            <%= kpi("check-circle", "green", "Resolved", resolved, "Completed jobs") %>
        <% } else if (isTenant) { %>
            <%= kpi("key", "gold", "Active leases", activeLeases, leases.size() + " lease record" + (leases.size() == 1 ? "" : "s")) %>
            <%= kpi("wrench", "red", "Open requests", open, resolved + " resolved") %>
            <%= kpi("file-text", "blue", "Applications", apps.size(), appsPending + " pending") %>
            <%= kpi("house", "green", "Homes available", available, "Ready to apply for") %>
        <% } else { %>
            <%= kpi("house", "gold", "My properties", props.size(), "Registered in your name") %>
            <%= kpi("key", "green", "Rented", rented, available + " available &middot; " + sold + " sold") %>
            <%= kpi("wrench", "red", "Open maintenance", open, high + " high priority") %>
            <%= kpi("trending-up", "blue", "Maintenance spend", money, "All requests on your properties") %>
        <% } %>
    </div>

    <div class="dash-grid">
        <!-- ============================== MAIN COLUMN ============================== -->
        <div class="dash-col">

            <% if (isOwner) { %>
            <div class="card">
                <div class="card-head"><div><h2>My properties</h2><p><%= props.size() %> in your portfolio</p></div>
                    <a class="link-more" href="PropertyListServlet">View all <%= Icon.svg("arrow-right") %></a></div>
                <% if (props.isEmpty()) { %>
                    <div class="empty"><div class="icon"><%= Icon.svg("house") %></div><p>No properties registered in your name yet.</p></div>
                <% } else { %>
                <div class="mini-list">
                    <% for (Property p : top(props, 5)) { %>
                    <a class="mini-item" href="PropertyServlet?action=view&propertyID=<%= p.getPropertyID() %>">
                        <span class="mini-thumb" style="background-image:url('<%= Html.propertyImage(p.getType(), p.getPropertyID()) %>')"></span>
                        <span class="mini-main"><strong><%= Html.esc(p.getAddress()) %></strong><span><%= Html.esc(p.getType()) %> &middot; <%= p.getSize() %> m&sup2; &middot; <%= p.getRooms() %> rooms</span></span>
                        <span class="<%= Html.badge(p.getStatus()) %>"><%= Html.esc(p.getStatus()) %></span>
                    </a>
                    <% } %>
                </div>
                <% } %>
            </div>
            <% } %>

            <% if (isTenant) { %>
            <div class="card">
                <div class="card-head"><div><h2>My home</h2><p>Your current and past leases</p></div>
                    <a class="link-more" href="PropertyListServlet">Details <%= Icon.svg("arrow-right") %></a></div>
                <% if (leases.isEmpty()) { %>
                    <div class="empty"><div class="icon"><%= Icon.svg("key") %></div><p>You don't have a lease yet.</p>
                        <a class="btn btn-primary btn-sm" href="PropertyServlet?action=list" style="margin-top:10px"><%= Icon.svg("search") %> Browse homes</a></div>
                <% } else { %>
                <div class="mini-list">
                    <% for (Map<String, String> l : top(leases, 4)) { %>
                    <div class="mini-item">
                        <span class="mini-icon"><%= Icon.svg("house") %></span>
                        <span class="mini-main"><strong><%= Html.esc(l.get("address")) %></strong><span><%= l.get("leaseStart") %> &rarr; <%= l.get("leaseEnd").isEmpty() ? "open-ended" : l.get("leaseEnd") %></span></span>
                        <span class="badge <%= "Active".equals(l.get("status")) ? "badge-active" : "" %>"><%= l.get("status") %></span>
                    </div>
                    <% } %>
                </div>
                <% } %>
            </div>
            <% } %>

            <div class="card">
                <div class="card-head">
                    <div><h2><%= isMaint ? "Work queue" : (isTenant ? "My maintenance requests" : "Recent maintenance") %></h2>
                        <p><%= open %> open &middot; <%= resolved %> resolved</p></div>
                    <a class="link-more" href="<%= isOwner ? "ReportServlet?action=maintenance" : "MaintenanceRequestServlet?action=list" %>">View all <%= Icon.svg("arrow-right") %></a>
                </div>
                <% if (requests.isEmpty()) { %>
                    <div class="empty"><div class="icon"><%= Icon.svg("wrench") %></div><p>No maintenance requests.</p></div>
                <% } else { %>
                <div class="table-wrap">
                    <table class="data compact">
                        <thead><tr><th>Issue</th><th><%= isTenant ? "Property" : "Tenant / property" %></th><th>Priority</th><th>Status</th><% if (isMaint) { %><th></th><% } %></tr></thead>
                        <tbody>
                        <% for (Map<String, String> r : top(requests, isMaint ? 8 : 5)) { %>
                            <tr>
                                <td class="cell-strong"><%= Html.esc(r.get("description")) %></td>
                                <td><% if (!isTenant) { %><%= Html.esc(r.get("tenantName")) %><br><% } %><span class="small muted"><%= Html.esc(r.get("address")) %></span></td>
                                <td><span class="<%= Html.badge(r.get("priority")) %>"><%= Html.esc(r.get("priority")) %></span></td>
                                <td><span class="<%= Html.badge(r.get("status")) %>"><%= "InProgress".equals(r.get("status")) ? "In progress" : Html.esc(r.get("status")) %></span></td>
                                <% if (isMaint) { %><td><a class="btn btn-sm btn-edit" href="MaintenanceRequestServlet?action=edit&requestID=<%= r.get("requestID") %>"><%= Icon.svg("pencil") %> Update</a></td><% } %>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>

            <% if (isStaff || isTenant) { %>
            <div class="card">
                <div class="card-head">
                    <div><h2><%= isTenant ? "My applications" : "Recent applications" %></h2><p><%= appsPending %> pending decision</p></div>
                    <a class="link-more" href="<%= isTenant ? "ApplicationServlet?action=myApplications" : "ApplicationServlet?action=list" %>">View all <%= Icon.svg("arrow-right") %></a>
                </div>
                <% if (apps.isEmpty()) { %>
                    <div class="empty"><div class="icon"><%= Icon.svg("file-text") %></div><p>No applications yet.</p></div>
                <% } else { %>
                <div class="table-wrap">
                    <table class="data compact">
                        <thead><tr><% if (!isTenant) { %><th>Applicant</th><% } %><th>Property</th><th>Date</th><th>Status</th><% if (!isTenant) { %><th></th><% } %></tr></thead>
                        <tbody>
                        <% for (Map<String, String> a : top(apps, 5)) { %>
                            <tr>
                                <% if (!isTenant) { %><td class="cell-strong"><%= Html.esc(a.get("applicantName")) %></td><% } %>
                                <td><%= Html.esc(a.get("address")) %></td>
                                <td class="nowrap"><%= a.get("date") %></td>
                                <td><span class="<%= Html.badge(a.get("status")) %>"><%= Html.esc(a.get("status")) %></span></td>
                                <% if (!isTenant) { %><td class="nowrap"><% if ("Pending".equals(a.get("status"))) { %>
                                    <a class="icon-btn ok" title="Approve" aria-label="Approve" href="ApplicationServlet?action=approve&applicationID=<%= a.get("applicationID") %>"><%= Icon.svg("check") %></a>
                                    <a class="icon-btn bad" title="Reject" aria-label="Reject" href="ApplicationServlet?action=reject&applicationID=<%= a.get("applicationID") %>"><%= Icon.svg("x") %></a>
                                <% } %></td><% } %>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>

        <!-- ============================== SIDE COLUMN ============================== -->
        <div class="dash-col">
            <% if (isStaff || isOwner) {
                   int tot = Math.max(props.size(), 1);
                   double c = 2 * Math.PI * 15.915;
                   double aLen = c * available / tot, rLen = c * rented / tot, sLen = c * sold / tot;
                   int occ = props.isEmpty() ? 0 : Math.round(rented * 100f / props.size());
            %>
            <div class="card">
                <div class="card-head"><div><h2>Occupancy</h2><p><%= props.size() %> <%= isOwner ? "of your properties" : "properties in total" %></p></div></div>
                <div class="donut-wrap">
                    <svg class="donut" viewBox="0 0 42 42" aria-hidden="true">
                        <circle cx="21" cy="21" r="15.915" fill="none" stroke="#eef2f7" stroke-width="5"/>
                        <circle cx="21" cy="21" r="15.915" fill="none" stroke="#c9a24a" stroke-width="5" stroke-dasharray="<%= String.format(Locale.ROOT, "%.2f %.2f", rLen, c - rLen) %>" stroke-dashoffset="25"/>
                        <circle cx="21" cy="21" r="15.915" fill="none" stroke="#15803d" stroke-width="5" stroke-dasharray="<%= String.format(Locale.ROOT, "%.2f %.2f", aLen, c - aLen) %>" stroke-dashoffset="<%= String.format(Locale.ROOT, "%.2f", 25 - rLen) %>"/>
                        <circle cx="21" cy="21" r="15.915" fill="none" stroke="#b91c1c" stroke-width="5" stroke-dasharray="<%= String.format(Locale.ROOT, "%.2f %.2f", sLen, c - sLen) %>" stroke-dashoffset="<%= String.format(Locale.ROOT, "%.2f", 25 - rLen - aLen) %>"/>
                    </svg>
                    <div class="donut-center"><strong><%= occ %>%</strong><span>rented</span></div>
                </div>
                <%= bar("Rented", rented, props.size(), "gold") %>
                <%= bar("Available", available, props.size(), "green") %>
                <%= bar("Sold", sold, props.size(), "red") %>
            </div>
            <% } %>

            <% if (isMaint || isStaff) { %>
            <div class="card">
                <div class="card-head"><div><h2>Open work by priority</h2><p><%= open %> open request<%= open == 1 ? "" : "s" %></p></div></div>
                <%= bar("High", high, open, "red") %>
                <%= bar("Medium", medium, open, "gold") %>
                <%= bar("Low", low, open, "green") %>
            </div>
            <% } %>

            <div class="card">
                <div class="card-head"><div><h2>Quick actions</h2><p>Shortcuts for your role</p></div></div>
                <div class="qa-list">
                    <% if (isStaff) { %>
                        <%= qa("PropertyServlet?action=add", "plus", "Add a property") %>
                        <%= qa("TenantServlet?action=form", "door-open", "Admit or release a tenant") %>
                        <%= qa("ApplicationServlet?action=new", "file-plus", "Capture an application") %>
                        <%= qa("MaintenanceRequestServlet?action=new", "wrench", "Log maintenance") %>
                        <% if (isAdmin || isManager) { %><%= qa("Reports.jsp", "download", "Export a report") %><% } %>
                        <% if (isAdmin) { %><%= qa("signupclient.jsp", "user-plus", "Add a team member") %><% } %>
                    <% } else if (isTenant) { %>
                        <%= qa("PropertyServlet?action=list", "search", "Browse available homes") %>
                        <%= qa("TenantAddApplication.jsp", "file-plus", "Apply for a property") %>
                        <%= qa("MaintenanceRequestServlet?action=new", "wrench", "Report a maintenance issue") %>
                        <%= qa("ApplicationServlet?action=myApplications", "file-text", "Track my applications") %>
                    <% } else if (isMaint) { %>
                        <%= qa("MaintenanceRequestServlet?action=list", "clipboard-list", "All work orders") %>
                        <%= qa("PropertyServlet?action=list", "house", "Property directory") %>
                    <% } else { %>
                        <%= qa("PropertyListServlet", "key", "View my properties") %>
                        <%= qa("ReportServlet?action=maintenance", "trending-up", "Maintenance report") %>
                        <%= qa("ReportServlet?action=exportPDF", "download", "Download report as PDF") %>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
