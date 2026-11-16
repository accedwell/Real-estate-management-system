<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    String role = (String) session.getAttribute("role");
    boolean canManage = "Admin".equals(role) || "Manager".equals(role) || "Agent".equals(role);
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (properties == null) { response.sendRedirect("PropertyServlet?action=list"); return; }
    String listTitle = (String) request.getAttribute("listTitle");
    int available = 0, rented = 0, sold = 0;
    for (Property p : properties) {
        if ("Available".equals(p.getStatus())) available++;
        else if ("Rented".equals(p.getStatus())) rented++;
        else if ("Sold".equals(p.getStatus())) sold++;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Properties | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-props">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Properties</div>
        <h1><%= listTitle != null ? Html.esc(listTitle) : "Property Listings" %></h1>
        <p><%= properties.size() %> properties &middot; <%= available %> available &middot; <%= rented %> rented &middot; <%= sold %> sold</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>

    <div class="card">
        <div class="card-head">
            <div class="toolbar" style="flex:1">
                <input type="text" id="q" placeholder="Search address, type or owner&hellip;" style="max-width:320px" oninput="filterCards()">
                <select id="st" style="max-width:180px" onchange="filterCards()">
                    <option value="">All statuses</option>
                    <option>Available</option><option>Rented</option><option>Sold</option>
                </select>
                <select id="sort" style="max-width:200px" onchange="sortCards()" aria-label="Sort properties">
                    <option value="">Sort: default order</option>
                    <option value="address">Address (A&ndash;Z)</option>
                    <option value="size">Size (largest first)</option>
                    <option value="rooms">Rooms (most first)</option>
                </select>
                <button type="button" class="chip" id="savedOnly" aria-pressed="false" onclick="toggleSaved()"><%= Icon.svg("heart") %> Saved <em id="savedCount">0</em></button>
            </div>
            <% if (canManage) { %>
                <a href="PropertyServlet?action=add" class="btn btn-primary"><%= Icon.svg("plus") %> Add property</a>
            <% } %>
        </div>

        <% if (properties.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("house") %></div><p>No properties found.</p></div>
        <% } else { %>
        <div class="prop-grid" id="grid">
            <% for (Property p : properties) { %>
            <article class="prop-card" data-id="<%= p.getPropertyID() %>" data-address="<%= Html.esc(String.valueOf(p.getAddress()).toLowerCase()) %>" data-size="<%= p.getSize() %>" data-rooms="<%= p.getRooms() %>" data-text="<%= Html.esc((p.getAddress() + " " + p.getType() + " " + p.getOwnerName()).toLowerCase()) %>" data-status="<%= Html.esc(p.getStatus()) %>">
                <a href="PropertyServlet?action=view&propertyID=<%= p.getPropertyID() %>">
                    <div class="ph" style="background-image:url('<%= Html.propertyImage(p.getType(), p.getPropertyID()) %>')">
                        <span class="<%= Html.badge(p.getStatus()) %>"><%= Html.esc(p.getStatus()) %></span>
                    </div>
                </a>
                <button type="button" class="fav" aria-pressed="false" aria-label="Save this property" title="Save this property" onclick="toggleFav(this)"><%= Icon.svg("heart") %></button>
                <div class="body">
                    <h3><a href="PropertyServlet?action=view&propertyID=<%= p.getPropertyID() %>" style="color:inherit"><%= Html.esc(p.getAddress()) %></a></h3>
                    <div class="meta">
                        <span><%= Icon.svg("house") %> <%= Html.esc(p.getType()) %></span>
                        <span><%= Icon.svg("ruler") %> <%= p.getSize() %> m&sup2;</span>
                        <span><%= Icon.svg("bed") %> <%= p.getRooms() %> room<%= p.getRooms() == 1 ? "" : "s" %></span>
                    </div>
                    <p class="small muted" style="margin-top:6px">Owner: <%= Html.escOr(p.getOwnerName(), "User " + p.getOwnerID()) %></p>
                    <% if (canManage) { %>
                    <div class="actions" style="display:flex; gap:6px; margin-top:12px">
                        <a class="btn btn-sm btn-edit" href="PropertyServlet?action=edit&propertyID=<%= p.getPropertyID() %>"><%= Icon.svg("pencil") %> Edit</a>
                        <a class="btn btn-sm btn-danger" href="PropertyServlet?action=remove&propertyID=<%= p.getPropertyID() %>"
                           onclick="return confirm('Delete this property? This cannot be undone.')"><%= Icon.svg("trash") %> Delete</a>
                    </div>
                    <% } else if ("Tenant".equals(role) && "Available".equals(p.getStatus())) { %>
                    <a class="btn btn-sm btn-primary" style="margin-top:12px" href="TenantAddApplication.jsp?propertyID=<%= p.getPropertyID() %>">Apply now</a>
                    <% } %>
                </div>
            </article>
            <% } %>
        </div>
        <div class="empty" id="none" style="display:none"><p>No properties match your search.</p></div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script>
    // ---- saved properties (remembered in this browser)
    var FAV = 'rems.savedProperties', savedOnly = false;
    function favs() { try { return JSON.parse(localStorage.getItem(FAV) || '[]'); } catch (e) { return []; } }
    function saveFavs(list) { try { localStorage.setItem(FAV, JSON.stringify(list)); } catch (e) { } }
    function paintFavs() {
        var list = favs(), n = 0;
        document.querySelectorAll('#grid .prop-card').forEach(function (c) {
            var on = list.indexOf(c.dataset.id) !== -1;
            if (on) n++;
            var b = c.querySelector('.fav');
            b.classList.toggle('on', on);
            b.setAttribute('aria-pressed', on ? 'true' : 'false');
            b.title = on ? 'Remove from saved' : 'Save this property';
        });
        var cnt = document.getElementById('savedCount');
        if (cnt) cnt.textContent = n;
    }
    function toggleFav(btn) {
        var id = btn.closest('.prop-card').dataset.id, list = favs(), i = list.indexOf(id);
        if (i === -1) list.push(id); else list.splice(i, 1);
        saveFavs(list);
        btn.classList.remove('pop-in'); void btn.offsetWidth; btn.classList.add('pop-in');
        paintFavs();
        filterCards();
    }
    function toggleSaved() {
        savedOnly = !savedOnly;
        var b = document.getElementById('savedOnly');
        b.classList.toggle('on', savedOnly);
        b.setAttribute('aria-pressed', savedOnly ? 'true' : 'false');
        filterCards();
    }
    // ---- sorting
    var original = null;
    function sortCards() {
        var grid = document.getElementById('grid');
        if (!grid) return;
        var cards = Array.prototype.slice.call(grid.querySelectorAll('.prop-card'));
        if (!original) original = cards.slice();
        var by = document.getElementById('sort').value;
        var sorted = !by ? original.slice() : cards.sort(function (a, b) {
            if (by === 'address') return a.dataset.address.localeCompare(b.dataset.address);
            return (+b.dataset[by]) - (+a.dataset[by]);
        });
        sorted.forEach(function (c) { grid.appendChild(c); });
    }

    function filterCards() {
        var q = document.getElementById('q').value.toLowerCase().trim();
        var st = document.getElementById('st').value;
        var shown = 0, list = favs();
        document.querySelectorAll('#grid .prop-card').forEach(function (c) {
            var ok = c.dataset.text.indexOf(q) !== -1 && (!st || c.dataset.status === st)
                    && (!savedOnly || list.indexOf(c.dataset.id) !== -1);
            c.style.display = ok ? '' : 'none';
            if (ok) shown++;
        });
        var none = document.getElementById('none');
        if (none) none.style.display = shown ? 'none' : 'block';
    }
    paintFavs();
</script>
</body>
</html>
