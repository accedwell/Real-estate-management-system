<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Html"%>
<%
    if (session.getAttribute("role") != null) {
        response.sendRedirect("Welcome.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>Create Account | REMS</title>
</head>
<body class="auth-body">
<div class="auth2">

    <!-- ============================== PHOTO PANEL ============================== -->
    <aside class="auth2-visual signup">
        <a class="auth2-brand" href="index.jsp">
            <%= Icon.logo() %>
            <span>REMS<small>Real Estate Management</small></span>
        </a>

        <div class="auth2-copy">
            <span class="auth2-pill"><%= Icon.svg("clock") %> Takes less than a minute</span>
            <h2>Find your next <span>home</span> with ease.</h2>
            <p>Create your account to browse available properties, apply online and log repairs whenever you need them.</p>

            <ul class="auth2-steps">
                <li><span>1</span><div><strong>Create your account</strong><small>Just a few details</small></div></li>
                <li><span>2</span><div><strong>Log in to your dashboard</strong><small>Everything in one place</small></div></li>
                <li><span>3</span><div><strong>Apply, track &amp; manage</strong><small>Applications, leases and repairs</small></div></li>
            </ul>
        </div>
    </aside>

    <!-- ============================== FORM PANEL ============================== -->
    <main class="auth2-panel">
        <div class="auth2-card wide">
            <a href="index.jsp" class="auth2-back"><%= Icon.svg("arrow-left") %> Back to home</a>

            <div class="auth2-head">
                <span class="auth2-mark"><%= Icon.svg("user-plus") %></span>
                <h1>Create your account</h1>
                <p>Already registered? <a href="Login.jsp">Log in here</a></p>
            </div>

            <%@ include file="/WEB-INF/jspf/alerts.jspf" %>

            <form action="RegisterServlet" method="POST" class="auth2-form" id="signup" novalidate data-loading-text="Creating account&hellip;">
                <input type="hidden" name="role" value="Tenant">

                <div class="field">
                    <label for="name">Full name</label>
                    <div class="input-icon"><%= Icon.svg("user") %>
                        <input type="text" id="name" name="name" placeholder="Thandi Mokoena" maxlength="100" required autocomplete="name"
                               value="<%= Html.esc(request.getParameter("name")) %>">
                    </div>
                </div>

                <div class="form-grid">
                    <div class="field">
                        <label for="email">Email address</label>
                        <div class="input-icon"><%= Icon.svg("mail") %>
                            <input type="email" id="email" name="email" placeholder="you@example.com" maxlength="100" required autocomplete="email"
                                   value="<%= Html.esc(request.getParameter("email")) %>">
                        </div>
                    </div>
                    <div class="field">
                        <label for="phone">Phone number</label>
                        <div class="input-icon"><%= Icon.svg("phone") %>
                            <input type="tel" id="phone" name="phone" placeholder="+27 82 123 4567" maxlength="20" required autocomplete="tel"
                                   pattern="[+0-9 ()\-]{7,20}" value="<%= Html.esc(request.getParameter("phone")) %>">
                        </div>
                    </div>
                </div>

                <div class="form-grid">
                    <div class="field">
                        <label for="password">Password</label>
                        <div class="input-icon has-toggle"><%= Icon.svg("lock") %>
                            <input type="password" id="password" name="password" minlength="6" placeholder="Min. 6 characters" required autocomplete="new-password">
                            <button type="button" class="pw-toggle" data-target="password" aria-label="Show password" title="Show password">
                                <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                            </button>
                        </div>
                        <div class="pw-meter" id="meter" data-score="0"><span></span><span></span><span></span><span></span></div>
                        <p class="pw-hint" id="meter-text">Mix letters, numbers &amp; symbols.</p>
                    </div>
                    <div class="field">
                        <label for="confirm">Confirm password</label>
                        <div class="input-icon has-toggle"><%= Icon.svg("lock") %>
                            <input type="password" id="confirm" name="confirm" minlength="6" placeholder="Repeat password" required autocomplete="new-password">
                            <button type="button" class="pw-toggle" data-target="confirm" aria-label="Show password" title="Show password">
                                <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                            </button>
                        </div>
                        <p class="pw-hint" id="match-text"></p>
                    </div>
                </div>

                <p class="form-error" id="form-error" hidden></p>

                <button type="submit" class="btn btn-dark btn-block btn-lg auth2-submit">
                    <span class="lbl"><%= Icon.svg("user-plus") %> Create account</span>
                    <span class="busy"><%= Icon.svg("loader", "i spin") %> Creating account&hellip;</span>
                </button>
            </form>

            <p class="auth2-foot"><%= Icon.svg("shield-check") %> Owner and staff accounts are created by an administrator.</p>
        </div>
    </main>
</div>
<script src="js/auth.js"></script>
</body>
</html>
