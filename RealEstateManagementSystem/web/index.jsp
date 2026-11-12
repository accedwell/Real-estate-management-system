<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Icon"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>REMS | Real Estate Management System</title>
    <meta name="description" content="Manage properties, tenants, leases, maintenance and reports in one place.">
    <link rel="icon" href="<%= Icon.FAVICON %>">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap">
    <link rel="stylesheet" href="css/app.css">
</head>
<body>

<!-- ===================== NAV ===================== -->
<header class="landing-nav">
    <div class="topbar-inner">
        <a class="brand" href="index.jsp">
            <%= Icon.logo() %>
            <span>REMS<small>Real Estate Management</small></span>
        </a>
        <nav class="nav-links">
            <a href="#features">Features</a>
            <a href="#roles">Who it's for</a>
            <a href="Login.jsp" class="btn btn-ghost btn-sm"><%= Icon.svg("log-in") %> Log in</a>
        </nav>
    </div>
</header>

<!-- ===================== HERO ===================== -->
<section class="hero">
    <div class="hero-slides" aria-hidden="true"><div></div><div></div><div></div></div>
    <div class="hero-content">
        <h1>Manage your properties <span>with ease.</span></h1>
        <p class="lead">One place for owners, tenants, agents, managers and maintenance teams across South Africa
            &mdash; listings, leases, applications, repairs and compliance reports.</p>

        <p id="error-display" class="alert alert-error" style="display:none; max-width:560px;"></p>

        <div class="cta">
            <a href="Login.jsp" class="btn btn-primary btn-lg"><%= Icon.svg("log-in") %> Log in to Dashboard</a>
            <a href="SignUp.jsp" class="btn btn-ghost btn-lg"><%= Icon.svg("user-plus") %> Create an Account</a>
        </div>
    </div>
</section>

<!-- ===================== FEATURES ===================== -->
<section class="section" id="features">
    <div class="section-inner">
        <div class="section-head">
            <span class="eyebrow">Everything in one system</span>
            <h2>Built for the whole property lifecycle</h2>
            <p>Replace spreadsheets with secure registers that every role can trust.</p>
        </div>
        <div class="features">
            <article class="feature">
                <div class="img" style="background-image:url('images/feature-listings.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("house") %> Smart Property Listings</h3>
                    <p>Capture houses, apartments and offices with size, rooms, owner and live status &mdash; Available, Rented or Sold.</p>
                </div>
            </article>
            <article class="feature">
                <div class="img" style="background-image:url('images/feature-tenants.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("users") %> Tenants &amp; Leases</h3>
                    <p>Screen rental applications, admit tenants, track lease dates and record departures in a few clicks.</p>
                </div>
            </article>
            <article class="feature">
                <div class="img" style="background-image:url('images/feature-maintenance.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("wrench") %> Maintenance Requests</h3>
                    <p>Tenants log issues instantly. Staff prioritise, update progress, record costs and resolution notes.</p>
                </div>
            </article>
            <article class="feature">
                <div class="img" style="background-image:url('images/feature-reports.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("chart") %> Reports &amp; Compliance</h3>
                    <p>Management summaries, lease, application and maintenance reports, exported to PDF, CSV or text.</p>
                </div>
            </article>
        </div>
    </div>
</section>

<!-- ===================== ROLES ===================== -->
<section class="section roles" id="roles">
    <div class="section-inner">
        <div class="section-head">
            <span class="eyebrow">Role-based access</span>
            <h2>A tailored dashboard for every role</h2>
            <p>Each user sees exactly the tools they need, and nothing they shouldn't.</p>
        </div>
        <div class="role-grid">
            <div class="role-card"><div class="ico"><%= Icon.svg("shield") %></div><h4>Administrator</h4><p>Users, governance and every register.</p></div>
            <div class="role-card"><div class="ico"><%= Icon.svg("briefcase") %></div><h4>Property Manager</h4><p>Applications, occupancy and reports.</p></div>
            <div class="role-card"><div class="ico"><%= Icon.svg("key") %></div><h4>Property Owner</h4><p>Portfolio and maintenance spend.</p></div>
            <div class="role-card"><div class="ico"><%= Icon.svg("building") %></div><h4>Real Estate Agent</h4><p>Capture listings and admit tenants.</p></div>
            <div class="role-card"><div class="ico"><%= Icon.svg("users") %></div><h4>Tenant</h4><p>Apply for homes and request repairs.</p></div>
            <div class="role-card"><div class="ico"><%= Icon.svg("wrench") %></div><h4>Maintenance Staff</h4><p>Work orders, costs and resolutions.</p></div>
        </div>
    </div>
</section>

<!-- ===================== CTA ===================== -->
<section class="cta-band">
    <h2>Ready to take control of your properties?</h2>
    <p>Sign up as a tenant or property owner in under a minute.</p>
    <div class="cta" style="display:flex; gap:14px; justify-content:center; flex-wrap:wrap;">
        <a href="SignUp.jsp" class="btn btn-primary btn-lg">Get Started <%= Icon.svg("arrow-right") %></a>
        <a href="Login.jsp" class="btn btn-ghost btn-lg">I already have an account</a>
    </div>
</section>

<footer class="site-footer">
    &copy; <span id="yr">2026</span> REMS &middot; Real Estate Management System &middot; Manage your property with ease
</footer>

<script>
    document.getElementById('yr').textContent = new Date().getFullYear();
    // Show an error passed in the URL (e.g. ?error=...) as plain text.
    var msg = new URLSearchParams(window.location.search).get('error');
    if (msg) {
        var box = document.getElementById('error-display');
        box.textContent = msg;
        box.style.display = 'block';
    }
</script>
</body>
</html>
