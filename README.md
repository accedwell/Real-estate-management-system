# Real Estate Management System (REMS)

**Manage your property with ease.**

[![Java EE](https://img.shields.io/badge/Java%20EE-Servlet%204%20%2F%20JSP-0b1b2b.svg)](https://javaee.github.io/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-24476a.svg)](https://www.postgresql.org/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-c9a24a.svg)](LICENSE)

REMS is a web application for managing rental properties in South Africa. Property listings, rental applications, leases, maintenance requests and reports all live in one system. Each user signs in to a dashboard built for their role.

Built with Java Servlets and JSP, PostgreSQL and OpenPDF. Runs on GlassFish or Apache Tomcat.

---

## Features

| Area | What you can do |
| :--- | :--- |
| **Landing page** | Public home page with a photo hero, a feature overview and links to log in or create an account. |
| **Accounts** | Log in with email and password. Tenants sign up themselves. Administrators create all other accounts. |
| **Dashboards** | A dashboard for each role with key figures, occupancy and priority charts, recent activity and quick actions. |
| **Properties** | Browse listings as photo cards with live search. View details. Add, edit and remove properties. Set the status to Available, Rented or Sold. |
| **Owners** | See every owner and the properties registered to them. Owners see only their own properties. |
| **Applications** | Tenants apply for available homes and track their applications. Staff approve or reject them, then admit the tenant. |
| **Tenants and leases** | Admit a tenant to a property with lease dates (the property becomes Rented), edit a lease, or end it. |
| **Maintenance** | Tenants log repairs on their own lease with a priority. Maintenance staff work through a queue and update the status, cost and notes. |
| **Reports** | Management summary, maintenance history (filtered by status and priority), tenant leases and applications. Export each as **PDF**, **CSV** or **TXT**. Every export is recorded in a report log. |
| **Users** | Administrators add, edit and remove users and assign roles. |
| **My profile** | Every user can update their name and phone number and change their password (the current password is required). |
| **Notifications** | A bell in the top bar shows role-specific updates, such as applications waiting, open or high-priority repairs, and a tenant's application decisions. |
| **Quick search** | Press **Ctrl + K** (or click the search box) to jump to any page or action. |
| **Productivity** | Click any table header to sort. Messages can be dismissed. Tenants can save favourite properties (the heart) and filter to saved homes. Properties can be sorted by address, size or rooms. |
| **Landing page** | "How it works" steps, a live **Featured homes** preview of available properties, and an FAQ. |

---

## Roles

| Role | Access |
| :--- | :--- |
| **Administrator** | Everything, including user management and the report log. |
| **Property Manager** | Properties, owners, tenants and leases, applications, maintenance and reports. |
| **Real Estate Agent** | Properties, owners, tenants and leases, applications and maintenance. |
| **Property Owner** | Their own properties, plus a maintenance report for those properties (with exports). |
| **Tenant** | Browse homes, apply, view their own leases and applications, and log maintenance requests. |
| **Maintenance Staff** | The maintenance work queue: update the status, cost and notes on each request. |

Access is enforced on the server by `AuthFilter`, page by page and action by action, not just hidden in the menu.

---

## Getting started

### Prerequisites
* JDK 8 or newer
* A Servlet 4 container: GlassFish 4.1+ (the NetBeans default) or Apache Tomcat 9
* PostgreSQL 12 or newer

### 1. Create the database
```sql
CREATE DATABASE "RealEstateManagementSystem";
```
Connect to it and run `The RealEstate Management System  DBscript.sql`. The script drops and recreates all tables, then loads demo data. Run it again at any time to reset the demo.

### 2. Point the app at your database (optional)
By default the app connects to `jdbc:postgresql://localhost:5432/RealEstateManagementSystem` as `postgres` / `123456`.
To use another database, set these environment variables (or `-D` JVM properties) on the server. You don't need to recompile:

| Variable | Example |
| :--- | :--- |
| `REMS_DB_URL` | `jdbc:postgresql://192.168.18.52:5432/RealEstateManagementSystem` |
| `REMS_DB_USER` | `postgres` |
| `REMS_DB_PASSWORD` | `your-password` |

### 3. Build and deploy
* **NetBeans:** open the `RealEstateManagementSystem` folder as a project, then choose **Clean and Build** and **Run**. The library jars are in `RealEstateManagementSystem/lib`.
* **Ready-made WAR:** deploy `RealEstateManagementSystem/dist/RealEstateManagementSystem.war` to GlassFish, or copy it into Tomcat's `webapps` folder.

Then open `http://localhost:8080/RealEstateManagementSystem/`.

### 4. Demo accounts
Every demo account uses the password **`Password123`**.

| Role | Email |
| :--- | :--- |
| Administrator | `admin@rems.co.za` |
| Property Manager | `diana@demo.com` |
| Real Estate Agent | `charlie@demo.com` |
| Property Owner | `alice@demo.com`, `sipho@demo.com` |
| Tenant | `bob@demo.com`, `lerato@demo.com`, `thabo@demo.com` |
| Maintenance Staff | `eddie@demo.com` |

Change or remove these accounts before using the system with real data.

---

## Deploy online (Render)

The repo includes a `Dockerfile` (Tomcat 9, app served at `/`) and a `render.yaml` blueprint that creates the web app and a PostgreSQL database together.

1. Push this repository to GitHub.
2. On [render.com](https://render.com), choose **New > Blueprint**, select the repository, then click **Apply**.
3. Wait for the first build to finish (about 5 minutes). On first start the app creates its tables and demo data automatically.
4. Open the `rems` service URL. Log in as `admin@rems.co.za` with the password stored in **rems > Environment > `REMS_ADMIN_PASSWORD`**. The other demo accounts use `Password123`.

Notes:
* On the free plan, the app sleeps after 15 minutes without traffic, and the first visit after that takes about a minute to wake it.
* Render's free PostgreSQL database expires 30 days after it is created unless you upgrade it.
* The app also understands a standard `DATABASE_URL` (`postgresql://user:password@host:port/db`), so the same Docker image runs on Railway, Fly.io or any Docker host.

---

## Project structure

```
RealEstateManagementSystem/
├── src/java/ac/za/tut/
│   ├── entities/   User, Property, Tenant, Application, MaintenanceRequest, Report
│   ├── dao/        Database access (one DAO per table, plus DBConnection)
│   ├── web/        Servlets and AuthFilter (access control)
│   └── util/       Html (escaping, badges) and Icon (SVG icons and logo)
├── web/
│   ├── *.jsp              Pages: landing, login, sign-up, dashboard, lists and forms
│   ├── WEB-INF/jspf/      Shared fragments: head, sidebar, alerts, footer, forms
│   ├── css/app.css        The single stylesheet for the whole app
│   ├── js/                Table search, and password helpers on the login and sign-up pages
│   └── images/            Background photography
├── lib/            OpenPDF and PostgreSQL JDBC driver
└── dist/           Built WAR file
```

---

## Security
* Every page except the landing, login and sign-up pages requires a session, and each role can reach only its own pages and actions.
* Passwords are stored as SHA-256 hashes. Public sign-up always creates a Tenant account, whatever the request contains.
* All user-entered text is HTML-escaped before it is displayed, and every query uses prepared statements.
* Session cookies are HttpOnly. The session ID is renewed at login, and protected pages are not cached, so pressing **Back** after logout does not reveal any data.

---

## Design
* **Colours:** a navy and gold palette used throughout.
  * Gold marks items waiting for action.
  * Navy marks items that are in progress or complete.
  * Grey marks items that are closed.
* **Icons:** inline SVG from the Lucide set, plus a custom REMS logo.
* **Font:** Poppins.
* **Layout:** a fixed sidebar with role-based menus and live counters. On phones the sidebar collapses into a slide-out menu.
* **Photos:** background photography in `web/images/` comes from [Unsplash](https://unsplash.com) and is used under the Unsplash License.

---

## License
Released under the [GNU General Public License v3.0](LICENSE).
