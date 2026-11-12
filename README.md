# -The-RealEstate-Management-System(REMS)
Manage Your Property With Ease
# Real Estate Management System (REMS)

[![Java EE](https://img.shields.io/badge/JavaEE-7.0-blue.svg)](https://javaee.github.io/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

An enterprise-grade property compliance and tracking engine tailored for the South African property management ecosystem. Built on a modular monolithic Java EE architecture, this application processes operational registers and structures multi-table log records into on-screen verification tables before compiling secure compliance document exports (`.pdf`, `.csv`, `.txt`).

---

## 👥 Authors & Contributors

*   **Mabane TS** — 231594760
*   **Mkhatshwa ML** — 224266499
*   **Khomu PN** — 224142218
*   **Aphane IT** — 221062167
*   **Sibambo AB** — 230547335
*   **Dlamini OS** — 224437382

---

## 👁️ System Vision & Objectives

The primary objective of the **Real Estate Management System (REMS)** is to replace legacy spreadsheet-based workflows with a unified collection of secure transactional registers and analytical reporting tools. 

Key functional objectives include:
*   **Property Registry Lifecycle:** Automated property data capture, historical data updates, and media/document correlation.
*   **Tenant Workflow Automation:** Simplified tenant screening, secure admission tracking, lease management, and structured tenant departure verification.
*   **Operational Logs:** Integrated maintenance request tracking, property inspections monitoring, and historical management registers.
*   **Compliance Analytics:** On-screen data previews paired with multi-format audit report compilation (`.pdf`, `.csv`, `.txt`) matching programmatic South African market requirements.

---

## 👤 System Actors

The platform isolates system interactions into six discrete role-based profiles:
1.  **Administrator:** Monitors system state telemetry, reviews operational logs, handles validation exceptions, and issues structural governance documents.
2.  **Property Manager:** Manages listing states, reviews application workflows, and monitors ongoing occupancy balances.
3.  **Property Owner:** Tracks active assets and monitors structural property allocation logs.
4.  **Real Estate Agent:** Captures incoming property assets, updates marketing data, links physical files/images, and logs active sales agreements.
5.  **Tenant:** Searches active listings, submits rental application forms, and files maintenance tickets.
6.  **Maintenance Staff:** Audits incoming maintenance requests, updates repair pipeline metrics, logs resource allocations, and commits engineering action reports.

---

## ⚡ Functional Requirements Matrix

*   **Authentication Tier:** Secure multi-role user registration and cryptographic user session authentication.
*   **Data Capture Layer:** Form validation structures for property details, ownership metrics, and structural specifications.
*   **Document Management Storage:** Relational mapping of media attachments, tenancy contracts, and physical inspection documents.
*   **Tenancy Lifecycle Pipelines:** Tracking engines handling onboarding profiles, active lease monitoring, dispute management, and departure slip generation.
*   **Maintenance Workflows:** Dynamic engineering tickets allowing status transformations, photo attachments, and prioritization updates.

---

## 📊 Behavioral Architecture: Event-Table Blueprint

| Event | Trigger Input | Source Actor | Core Use Case | System Response | Destination |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Buyer applies for property** | Submits property application | Buyer | Property Application | Logs application data to database | Database / Admin |
| **Administrator reviews application** | Opens pending applications list | Administrator | Review Application | Compiles application approval/rejection state | Buyer / Admin |
| **Tenant submits rental application** | Submits localized rental form | Tenant | Rental Application | Logs rental contract application row | Database / Admin |
| **Administrator reviews rental app** | Requests active application queue | Administrator | Review Application | Commits lease approval/rejection vector | Tenant / Admin |
| **Property Agent captures property** | Enters building details profile | Property Agent | Capture Property Data | Generates new active property registry row | Database |
| **Agent uploads property photo** | Attaches structural image media | Property Agent | Upload Property Photo | Persists media array to record mapping | Database |
| **Tenant admitted to property** | Assigns tenant profile to unit | Data Clerk | Record Tenant Submission | Transforms target property state mapping | Database |
| **Property sale recorded** | Completes entry profile | Property Agent | Record Property Sale | Commits asset handoff logging row | Database / Admin |
| **Tenant submits maintenance request** | Requests utility repair help | Tenant | Record Maintenance Request| Inserts active engineering ticket row | Database / Admin |
| **Administrator generates property report**| Invokes property asset breakdown | Administrator | Print Property Report | Compiles real-time property catalog output | Administrator |
| **Administrator generates tenant report**  | Invokes active lease audit trail | Administrator | Print Tenant Report | Compiles live tenancy summary ledger | Administrator |
| **Administrator generates sales report**   | Invokes historical ledger review | Administrator | Print Sales Report | Compiles historical volume summary output | Administrator |
| **Administrator generates maintenance report**| Invokes engineering ticket log | Administrator | Print Maintenance Report | Compiles status and timeline metrics | Administrator |

---

## 📝 Use Case Specifications

### 1. Capture Property Data
*   **Triggering Event:** A Property Manager decides to add a new property listing into the system.
*   **Brief Description:** A real estate agent records complete property information (owner details, property specifications, and images) into the system so that the property can be listed for sale or rent.
*   **Primary Actor:** Property Manager
*   **Related Use Cases:** `Validate Owner Details`, `Upload Property Images`, `Verify Property Information`
*   **Pre-conditions:**
    *   Property owner has submitted the property for listing.
    *   The agent is authenticated and logged into the system.
    *   The property does not already exist in the system.
    *   Required property documentation is available.
*   **Post-conditions:**
    *   Property record is successfully stored in the system database.
    *   Property becomes available for search and listing.
    *   Confirmation is displayed to the agent.
*   **Flow of Activities:**


---

## 🗺️ Implementation WBS (Work Breakdown Structure)

The execution map for bringing the system into software production is planned according to the following system lifecycles:

*   **Phase 1: Foundation & Data Modeling (Sprint 1)**
    *   Initialize structural relational physical schema scripts for database containment.
    *   Establish structural entity mappings (`Report.java`, `User.java`, `Property.java`).
    *   Establish baseline low-level database connection factories (`DBConnection.java`).
*   **Phase 2: Persistent Storage Layer Development (Sprint 2)**
    *   Code internal structural data processing methods inside `ReportDAO.java`.
    *   Optimize multi-table data processing connections using optimized `JOIN` operations.
    *   Build localized parameter arrays matching regional filter actions.
*   **Phase 3: Controller Routing & Document Streams (Sprint 3)**
    *   Initialize transaction endpoints inside the central controller layer (`ReportServlet.java`).
    *   Incorporate OpenPDF document compilation modules for compiling formatting routines.
    *   Secure low-level memory parameters against streaming context thread block blocks.
*   **Phase 4: Web Presentation Integration (Sprint 4)**
    *   Refine structural compilation frameworks across view pages (`Report.jsp`).
    *   Enforce layout preservation guidelines using CSS layout boundaries.
    *   Validate asynchronous messaging parameters across front-to-back operations.

---

## ⚙️ Local Development Setup

### Prerequisites
*   JDK 8 or newer
*   A Java EE / Servlet 4 container: GlassFish 4.1+ (NetBeans default) or Apache Tomcat 9
*   PostgreSQL 12 or newer

### 1. Create the database
```sql
CREATE DATABASE "RealEstateManagementSystem";
```
Connect to it and run `The RealEstate Management System  DBscript.sql`. The script drops and recreates all tables and loads demo data.

### 2. Point the app at your database (optional)
The app connects to `jdbc:postgresql://localhost:5432/RealEstateManagementSystem` as `postgres` / `123456` by default.
To use something else, set these environment variables (or `-D` JVM properties) on the server. No recompile is needed:

| Variable | Example |
| :--- | :--- |
| `REMS_DB_URL` | `jdbc:postgresql://192.168.18.52:5432/RealEstateManagementSystem` |
| `REMS_DB_USER` | `postgres` |
| `REMS_DB_PASSWORD` | `your-password` |

### 3. Build and deploy
*   **NetBeans:** open the `RealEstateManagementSystem` folder as a project, then choose **Clean and Build**, then **Run**. Library jars are in `RealEstateManagementSystem/lib`.
*   **Ready-made WAR:** deploy `RealEstateManagementSystem/dist/RealEstateManagementSystem.war` to GlassFish or copy it into Tomcat's `webapps` folder.

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

Public sign-up creates **Tenant** accounts only. Owner and staff accounts are created by an administrator under **Users** or **Add Member**.

---

## 🔐 Security
*   Every page except the landing, login and sign-up pages requires a session. `AuthFilter` also enforces role-based access to each page and action.
*   Passwords are stored as SHA-256 hashes. All user-supplied text is HTML-escaped before display, and every query uses prepared statements.
*   Session cookies are HttpOnly, and the session ID is renewed at login.

---

## 🎨 Design
All pages share one stylesheet (`web/css/app.css`) and common fragments in `web/WEB-INF/jspf/` (head, navigation, alerts, footer and shared forms).
Background photography in `web/images/` comes from [Unsplash](https://unsplash.com) and is used under the Unsplash License.
