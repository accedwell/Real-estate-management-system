<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%
    // Already logged in? Go straight to the dashboard.
    if (session.getAttribute("role") != null) {
        response.sendRedirect("Welcome.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Log in | REMS</title>
</head>
<body class="auth-body">
<div class="auth2">

    <!-- ============================== PHOTO PANEL ============================== -->
    <aside class="auth2-visual">
        <a class="auth2-brand" href="index.jsp">
            <%= Icon.logo() %>
            <span>REMS<small>Real Estate Management</small></span>
        </a>

        <div class="auth2-copy">
            <span class="auth2-pill"><%= Icon.svg("shield-check") %> Secure role-based access</span>
            <h2>Welcome home to <span>smarter</span> property management.</h2>
            <p>Listings, leases, maintenance and reports &mdash; all in one secure dashboard for owners, tenants and teams.</p>

            <div class="glass-card">
                <div class="glass-stat"><span class="glass-ico"><%= Icon.svg("house") %></span><div><strong>Listings</strong><small>Live availability</small></div></div>
                <div class="glass-stat"><span class="glass-ico"><%= Icon.svg("wrench") %></span><div><strong>Repairs</strong><small>Live tracking</small></div></div>
                <div class="glass-stat"><span class="glass-ico"><%= Icon.svg("download") %></span><div><strong>Reports</strong><small>PDF &middot; CSV &middot; TXT</small></div></div>
            </div>
        </div>
    </aside>

    <!-- ============================== FORM PANEL ============================== -->
    <main class="auth2-panel">
        <div class="auth2-card">
            <a href="index.jsp" class="auth2-back"><%= Icon.svg("arrow-left") %> Back to home</a>

            <div class="auth2-head">
                <span class="auth2-mark"><%= Icon.svg("log-in") %></span>
                <h1>Welcome back</h1>
                <p>Log in to continue to your dashboard.</p>
            </div>

            <%@ include file="/WEB-INF/jspf/alerts.jspf" %>

            <form action="LoginServlet" method="POST" autocomplete="on" class="auth2-form" data-loading-text="Signing in&hellip;">
                <div class="field">
                    <label for="username">Email address</label>
                    <div class="input-icon">
                        <%= Icon.svg("mail") %>
                        <input type="email" id="username" name="username" placeholder="you@example.com" required autofocus
                               autocomplete="username" value="<%= Html.esc(request.getParameter("email")) %>">
                    </div>
                </div>
                <div class="field">
                    <label for="password">Password</label>
                    <div class="input-icon has-toggle">
                        <%= Icon.svg("lock") %>
                        <input type="password" id="password" name="password" placeholder="Enter your password" required autocomplete="current-password">
                        <button type="button" class="pw-toggle" data-target="password" aria-label="Show password" title="Show password">
                            <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                        </button>
                    </div>
                    <p class="caps-warn" id="caps" hidden><%= Icon.svg("alert") %> Caps Lock is on</p>
                </div>

                <button type="submit" class="btn btn-dark btn-block btn-lg auth2-submit">
                    <span class="lbl"><%= Icon.svg("log-in") %> Sign in</span>
                    <span class="busy"><%= Icon.svg("loader", "i spin") %> Signing in&hellip;</span>
                </button>
            </form>

            <div class="auth2-divider"><span>New to REMS?</span></div>
            <a href="SignUp.jsp" class="btn btn-outline btn-block"><%= Icon.svg("user-plus") %> Create a free account</a>

            <p class="auth2-foot"><%= Icon.svg("lock") %> Passwords are stored encrypted.</p>
        </div>
    </main>
</div>
<script src="js/auth.js"></script>
</body>
</html>
