<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ac.za.tut.util.Icon"%>
<%@page import="ac.za.tut.util.Html"%>
<%@page import="ac.za.tut.entities.Property"%>
<%@page import="ac.za.tut.dao.PropertyDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%
    // Up to six available homes for the public "Featured homes" preview.
    List<Property> featured = new ArrayList<>();
    int totalHomes = 0;
    try {
        for (Property fp : new PropertyDAO().getAllProperties()) {
            totalHomes++;
            if ("Available".equals(fp.getStatus()) && featured.size() < 6) featured.add(fp);
        }
    } catch (Exception ignored) { /* the section is simply hidden if the database is unavailable */ }
%>
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
            <a href="#how">How it works</a>
            <% if (!featured.isEmpty()) { %><a href="#homes">Homes</a><% } %>
            <a href="#roles">Who it's for</a>
            <a href="#faq">FAQ</a>
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
        <div class="section-head reveal">
            <span class="eyebrow">Everything in one system</span>
            <h2>Built for the whole property lifecycle</h2>
            <p>Replace spreadsheets with secure registers that every role can trust.</p>
        </div>
        <div class="features">
            <article class="feature reveal">
                <div class="img" style="background-image:url('images/feature-listings.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("house") %> Smart Property Listings</h3>
                    <p>Capture houses, apartments and offices with size, rooms, owner and live status &mdash; Available, Rented or Sold.</p>
                </div>
            </article>
            <article class="feature reveal">
                <div class="img" style="background-image:url('images/feature-tenants.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("users") %> Tenants &amp; Leases</h3>
                    <p>Screen rental applications, admit tenants, track lease dates and record departures in a few clicks.</p>
                </div>
            </article>
            <article class="feature reveal">
                <div class="img" style="background-image:url('images/feature-maintenance.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("wrench") %> Maintenance Requests</h3>
                    <p>Tenants log issues instantly. Staff prioritise, update progress, record costs and resolution notes.</p>
                </div>
            </article>
            <article class="feature reveal">
                <div class="img" style="background-image:url('images/feature-reports.jpg')"></div>
                <div class="txt">
                    <h3><%= Icon.svg("chart") %> Reports &amp; Compliance</h3>
                    <p>Management summaries, lease, application and maintenance reports, exported to PDF, CSV or text.</p>
                </div>
            </article>
        </div>
    </div>
</section>

<!-- ===================== HOW IT WORKS ===================== -->
<section class="section how" id="how">
    <div class="section-inner">
        <div class="section-head reveal">
            <span class="eyebrow">Simple from day one</span>
            <h2>How it works</h2>
            <p>From finding a home to fixing a leaking tap &mdash; three steps, one system.</p>
        </div>
        <ol class="steps">
            <li class="step reveal">
                <span class="step-ico"><%= Icon.svg("user-plus") %></span>
                <span class="step-no">01</span>
                <h3>Create your account</h3>
                <p>Sign up in under a minute. Owners and staff are set up by an administrator with the right access.</p>
            </li>
            <li class="step reveal">
                <span class="step-ico"><%= Icon.svg("search") %></span>
                <span class="step-no">02</span>
                <h3>Find &amp; apply</h3>
                <p>Browse available homes with photos, size and rooms, then apply online and follow your application live.</p>
            </li>
            <li class="step reveal">
                <span class="step-ico"><%= Icon.svg("key") %></span>
                <span class="step-no">03</span>
                <h3>Move in &amp; manage</h3>
                <p>Once approved you're admitted to your lease. Log repairs any time and track them until they're resolved.</p>
            </li>
        </ol>
    </div>
</section>

<% if (!featured.isEmpty()) { %>
<!-- ===================== FEATURED HOMES ===================== -->
<section class="section homes" id="homes">
    <div class="section-inner">
        <div class="section-head reveal">
            <span class="eyebrow">Available now</span>
            <h2>Featured homes</h2>
            <p><%= featured.size() %> of <%= totalHomes %> listed properties are ready for new tenants today.</p>
        </div>
        <div class="prop-grid">
            <% for (Property fp : featured) { %>
            <article class="prop-card reveal">
                <a href="Login.jsp?info=<%= java.net.URLEncoder.encode("Log in or create an account to view and apply for this home.", "UTF-8") %>">
                    <div class="ph" style="background-image:url('<%= Html.propertyImage(fp.getType(), fp.getPropertyID()) %>')">
                        <span class="<%= Html.badge(fp.getStatus()) %>"><%= Html.esc(fp.getStatus()) %></span>
                    </div>
                </a>
                <div class="body">
                    <h3><%= Html.esc(fp.getAddress()) %></h3>
                    <div class="meta">
                        <span><%= Icon.svg("house") %> <%= Html.esc(fp.getType()) %></span>
                        <span><%= Icon.svg("ruler") %> <%= fp.getSize() %> m&sup2;</span>
                        <span><%= Icon.svg("bed") %> <%= fp.getRooms() %> room<%= fp.getRooms() == 1 ? "" : "s" %></span>
                    </div>
                    <a class="btn btn-sm btn-outline home-cta" href="SignUp.jsp"><%= Icon.svg("file-plus") %> Apply for this home</a>
                </div>
            </article>
            <% } %>
        </div>
    </div>
</section>
<% } %>

<!-- ===================== ROLES ===================== -->
<section class="section roles" id="roles">
    <div class="section-inner">
        <div class="section-head reveal">
            <span class="eyebrow">Role-based access</span>
            <h2>A tailored dashboard for every role</h2>
            <p>Each user sees exactly the tools they need, and nothing they shouldn't.</p>
        </div>
        <div class="role-grid">
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("shield") %></div><h4>Administrator</h4><p>Users, governance and every register.</p></div>
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("briefcase") %></div><h4>Property Manager</h4><p>Applications, occupancy and reports.</p></div>
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("key") %></div><h4>Property Owner</h4><p>Portfolio and maintenance spend.</p></div>
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("building") %></div><h4>Real Estate Agent</h4><p>Capture listings and admit tenants.</p></div>
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("users") %></div><h4>Tenant</h4><p>Apply for homes and request repairs.</p></div>
            <div class="role-card reveal"><div class="ico"><%= Icon.svg("wrench") %></div><h4>Maintenance Staff</h4><p>Work orders, costs and resolutions.</p></div>
        </div>
    </div>
</section>

<!-- ===================== FAQ ===================== -->
<section class="section faq" id="faq">
    <div class="section-inner faq-inner">
        <div class="section-head reveal">
            <span class="eyebrow">Good to know</span>
            <h2>Frequently asked questions</h2>
            <p>Still unsure? These are the questions we hear most.</p>
        </div>
        <div class="faq-list">
            <details class="reveal" open>
                <summary>Who can create an account?<%= Icon.svg("chevron-down", "i chev") %></summary>
                <p>Anyone looking for a home can sign up as a tenant. Property owners, agents, managers and maintenance staff are added by an administrator so they get the right access.</p>
            </details>
            <details class="reveal">
                <summary>How do I apply for a property?<%= Icon.svg("chevron-down", "i chev") %></summary>
                <p>Log in, open <strong>Properties</strong>, choose an available home and click <strong>Apply now</strong>. You can follow the status under <strong>My Applications</strong>.</p>
            </details>
            <details class="reveal">
                <summary>How do I report something that needs fixing?<%= Icon.svg("chevron-down", "i chev") %></summary>
                <p>Tenants click <strong>Report an issue</strong>, describe the problem and pick a priority. Maintenance staff see it immediately and update it until it's resolved.</p>
            </details>
            <details class="reveal">
                <summary>Can I download reports?<%= Icon.svg("chevron-down", "i chev") %></summary>
                <p>Yes. Managers and administrators can export management, lease, application and maintenance reports as PDF, CSV or text. Owners get a maintenance report for their own properties.</p>
            </details>
            <details class="reveal">
                <summary>Is my information safe?<%= Icon.svg("chevron-down", "i chev") %></summary>
                <p>Passwords are stored encrypted, every page checks your role before showing anything, and you only ever see the data that belongs to you.</p>
            </details>
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

<button class="to-top" type="button" aria-label="Back to top"><%= Icon.svg("arrow-up") %></button>

<footer class="site-footer">
    &copy; <span id="yr">2026</span> REMS &middot; Real Estate Management System &middot; Manage your property with ease
</footer>

<script>
    // Fade sections in as they scroll into view, and show the back-to-top button.
    (function () {
        var els = document.querySelectorAll('.reveal');
        if ('IntersectionObserver' in window && !window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
            document.documentElement.classList.add('reveal-on');
            var io = new IntersectionObserver(function (entries) {
                entries.forEach(function (en) {
                    if (en.isIntersecting) { en.target.classList.add('in'); io.unobserve(en.target); }
                });
            }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });
            els.forEach(function (el, i) { el.style.transitionDelay = (i % 3) * 80 + 'ms'; io.observe(el); });
        }
        var top = document.querySelector('.to-top');
        top.addEventListener('click', function () { window.scrollTo({ top: 0, behavior: 'smooth' }); });
        window.addEventListener('scroll', function () { top.classList.toggle('show', window.scrollY > 600); }, { passive: true });
    })();
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
