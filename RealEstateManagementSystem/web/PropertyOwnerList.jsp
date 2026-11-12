<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.User"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="java.util.List"%>
<%
    List<User> owners = (List<User>) request.getAttribute("owners");
    if (owners == null) { response.sendRedirect("PropertyServlet?action=listOwners"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Property Owners | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / Owners</div>
        <h1>Property Owners</h1>
        <p><%= owners.size() %> registered owner<%= owners.size() == 1 ? "" : "s" %>. Open a portfolio to see their properties.</p>
    </div>
</section>

<main class="page">
    <div class="card">
        <% if (owners.isEmpty()) { %>
            <div class="empty"><div class="icon"><%= Icon.svg("users") %></div><p>No property owners are registered yet.</p></div>
        <% } else { %>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Owner</th><th>Email</th><th>Phone</th><th></th></tr></thead>
                <tbody>
                <% for (User o : owners) { %>
                    <tr>
                        <td><div style="display:flex; align-items:center; gap:10px"><span class="avatar"><%= Html.initial(o.getName()) %></span><strong><%= Html.esc(o.getName()) %></strong></div></td>
                        <td><a href="mailto:<%= Html.esc(o.getEmail()) %>"><%= Html.esc(o.getEmail()) %></a></td>
                        <td><%= Html.escOr(o.getPhone(), "—") %></td>
                        <td style="text-align:right"><a class="btn btn-sm btn-dark" href="PropertyServlet?action=viewByOwner&ownerID=<%= o.getUserID() %>"><%= Icon.svg("eye") %> View portfolio</a></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
