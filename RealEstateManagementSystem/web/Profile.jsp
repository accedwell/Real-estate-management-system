<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.entities.User"%>
<%
    User me = (User) request.getAttribute("user");
    if (me == null) { response.sendRedirect("ProfileServlet"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>My Profile | REMS</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/nav.jspf" %>

<section class="page-hero h-users">
    <div class="page-hero-inner">
        <div class="crumbs"><a href="Welcome.jsp">Dashboard</a> / My profile</div>
        <h1>My profile</h1>
        <p>Keep your contact details up to date and your account secure.</p>
    </div>
</section>

<main class="page">
    <%@ include file="/WEB-INF/jspf/alerts.jspf" %>

    <div class="profile-grid">
        <!-- ---------------------------------------------------- identity card -->
        <aside class="card profile-card">
            <div class="profile-cover"></div>
            <span class="avatar avatar-xl"><%= Html.initial(me.getName()) %></span>
            <h2><%= Html.esc(me.getName()) %></h2>
            <span class="badge badge-role-<%= me.getRole() == null ? "" : me.getRole().toLowerCase() %>"><%= navRoleLabel %></span>

            <ul class="profile-facts">
                <li><%= Icon.svg("mail") %><div><small>Email (used to log in)</small><strong><%= Html.esc(me.getEmail()) %></strong></div></li>
                <li><%= Icon.svg("phone") %><div><small>Phone</small><strong><%= Html.escOr(me.getPhone(), "Not added yet") %></strong></div></li>
                <li><%= Icon.svg("shield-check") %><div><small>Access level</small><strong><%= navRoleLabel %></strong></div></li>
            </ul>
            <p class="small muted profile-note"><%= Icon.svg("info") %> To change your email or role, contact an administrator.</p>
        </aside>

        <!-- ---------------------------------------------------- settings -->
        <section class="card profile-main">
            <div class="tabs" role="tablist">
                <a class="tab active" href="#details" data-tab="details" role="tab"><%= Icon.svg("user") %> Personal details</a>
                <a class="tab" href="#security" data-tab="security" role="tab"><%= Icon.svg("lock") %> Password &amp; security</a>
            </div>

            <div class="tab-panel" id="tab-details">
                <form action="ProfileServlet" method="POST">
                    <input type="hidden" name="action" value="details">
                    <div class="field">
                        <label for="pname">Full name</label>
                        <div class="input-icon"><%= Icon.svg("user") %>
                            <input type="text" id="pname" name="name" maxlength="100" required value="<%= Html.esc(me.getName()) %>">
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field">
                            <label for="pemail">Email address</label>
                            <div class="input-icon"><%= Icon.svg("mail") %>
                                <input type="email" id="pemail" value="<%= Html.esc(me.getEmail()) %>" disabled>
                            </div>
                        </div>
                        <div class="field">
                            <label for="pphone">Phone number</label>
                            <div class="input-icon"><%= Icon.svg("phone") %>
                                <input type="tel" id="pphone" name="phone" maxlength="20" pattern="[+0-9 ()\-]{7,20}" placeholder="+27 82 123 4567"
                                       value="<%= Html.esc(me.getPhone()) %>">
                            </div>
                        </div>
                    </div>
                    <div class="form-actions">
                        <button type="reset" class="btn btn-outline">Undo changes</button>
                        <button type="submit" class="btn btn-dark"><%= Icon.svg("check") %> Save details</button>
                    </div>
                </form>
            </div>

            <div class="tab-panel" id="tab-security" hidden>
                <form action="ProfileServlet" method="POST" id="pwform">
                    <input type="hidden" name="action" value="password">
                    <div class="field">
                        <label for="current">Current password</label>
                        <div class="input-icon has-toggle"><%= Icon.svg("lock") %>
                            <input type="password" id="current" name="current" required autocomplete="current-password" placeholder="Enter your current password">
                            <button type="button" class="pw-toggle" data-target="current" aria-label="Show password" title="Show password">
                                <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                            </button>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field">
                            <label for="password">New password</label>
                            <div class="input-icon has-toggle"><%= Icon.svg("lock") %>
                                <input type="password" id="password" name="password" minlength="6" required autocomplete="new-password" placeholder="Min. 6 characters">
                                <button type="button" class="pw-toggle" data-target="password" aria-label="Show password" title="Show password">
                                    <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                                </button>
                            </div>
                            <div class="pw-meter" id="meter" data-score="0"><span></span><span></span><span></span><span></span></div>
                            <p class="pw-hint" id="meter-text">Mix letters, numbers &amp; symbols.</p>
                        </div>
                        <div class="field">
                            <label for="confirm">Confirm new password</label>
                            <div class="input-icon has-toggle"><%= Icon.svg("lock") %>
                                <input type="password" id="confirm" name="confirm" minlength="6" required autocomplete="new-password" placeholder="Repeat password">
                                <button type="button" class="pw-toggle" data-target="confirm" aria-label="Show password" title="Show password">
                                    <span class="show"><%= Icon.svg("eye") %></span><span class="hide"><%= Icon.svg("eye-off") %></span>
                                </button>
                            </div>
                            <p class="pw-hint" id="match-text"></p>
                        </div>
                    </div>
                    <div class="tip-box"><%= Icon.svg("shield-check") %>
                        <div><strong>Tips for a strong password</strong>
                            <span>Use 10+ characters with upper- and lower-case letters, a number and a symbol. Don't reuse a password from another site.</span></div>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-dark"><%= Icon.svg("lock") %> Change password</button>
                    </div>
                </form>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="js/auth.js"></script>
<script>
    (function () {
        var tabs = document.querySelectorAll('.tabs .tab');
        function show(name) {
            if (name !== 'security') name = 'details';
            tabs.forEach(function (t) { t.classList.toggle('active', t.dataset.tab === name); });
            document.getElementById('tab-details').hidden = name !== 'details';
            document.getElementById('tab-security').hidden = name !== 'security';
        }
        tabs.forEach(function (t) {
            t.addEventListener('click', function (e) { e.preventDefault(); history.replaceState(null, '', '#' + t.dataset.tab); show(t.dataset.tab); });
        });
        show(location.hash.replace('#', ''));
        document.getElementById('pwform').addEventListener('submit', function (e) {
            if (document.getElementById('password').value !== document.getElementById('confirm').value) {
                e.preventDefault();
                document.getElementById('confirm').focus();
            }
        });
    })();
</script>
</body>
</html>
