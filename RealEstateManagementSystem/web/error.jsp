<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
    boolean notFound = code != null && code == 404;
    if (exception != null) { application.log("REMS error", exception); }
    boolean loggedIn = session != null && session.getAttribute("role") != null;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title><%= notFound ? "Page not found" : "Something went wrong" %> | REMS</title>
</head>
<body>
<section class="hero" style="min-height:100vh">
    <div class="hero-slides" aria-hidden="true"><div></div></div>
    <div class="hero-content" style="text-align:center">
        <span class="eyebrow"><%= code != null ? "Error " + code : "Error" %></span>
        <h1 style="margin:18px auto 14px"><%= notFound ? "We couldn't find that page." : "Something went wrong." %></h1>
        <p class="lead" style="margin:0 auto 30px">
            <%= notFound ? "The page may have moved, or the link is out of date."
                         : "The system could not complete your request. Please try again, and contact the administrator if it keeps happening (check that the database is running)." %>
        </p>
        <div class="cta" style="justify-content:center">
            <a href="<%= loggedIn ? "Welcome.jsp" : "index.jsp" %>" class="btn btn-primary btn-lg"><%= loggedIn ? "Back to dashboard" : "Back to home" %></a>
        </div>
    </div>
</section>
</body>
</html>
