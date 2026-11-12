package ac.za.tut.web;

import ac.za.tut.dao.ReportDAO;
import ac.za.tut.entities.Report;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Final Unified ReportServlet handling multi-table data processing,
 * individual validation metrics, and multi-format reporting exports.
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/ReportServlet"})
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        ReportDAO dao = new ReportDAO();

        // Parse common metadata values passed from the generation form
        String generatedByParam = request.getParameter("generatedBy");
        Integer sessionUser = (Integer) request.getSession().getAttribute("userID");
        int generatedBy = PropertyServlet.parseInt(generatedByParam, sessionUser != null ? sessionUser : 1);
        
        String dateParam = request.getParameter("date");
        java.sql.Date logDate;
        try {
            logDate = java.sql.Date.valueOf(dateParam);
        } catch (Exception e) {
            logDate = java.sql.Date.valueOf(LocalDate.now());
        }

        try {
            // =========================================================
            // REPORT 1: MULTI-TABLE MAINTENANCE WORKLOG PAGE
            // =========================================================
            if ("maintenance".equalsIgnoreCase(action)) {
                HttpSession session = request.getSession();
                Integer userID = (Integer) session.getAttribute("userID");
                String role = (String) session.getAttribute("role");
                
                if (userID == null) { userID = 1; role = "Owner"; }

                String finalStatus = request.getParameter("status") != null && !request.getParameter("status").isEmpty() ? request.getParameter("status") : "ALL";
                String finalPriority = request.getParameter("priority") != null && !request.getParameter("priority").isEmpty() ? request.getParameter("priority") : "ALL";

                request.setAttribute("filterStatus", finalStatus);
                request.setAttribute("filterPriority", finalPriority);

                List<Map<String, String>> data = dao.getMaintenanceReport(userID, role, finalStatus, finalPriority);
                request.setAttribute("reportData", data);
                request.getRequestDispatcher("property_maintenance_report.jsp").forward(request, response);

            // =========================================================
            // REPORT 2: EXECUTIVE OVERHEAD SUMMARY METRICS
            // =========================================================
            } else if ("summary".equalsIgnoreCase(action)) {
                Map<String, Integer> metrics = dao.getSummaryMetrics();
                request.setAttribute("metrics", metrics);
                request.getRequestDispatcher("summary_report.jsp").forward(request, response);
                
            // =========================================================
            // REPORT 3: RELATIONAL ACTIVE TENANT LEASES LIST
            // =========================================================
            } else if ("tenantLease".equalsIgnoreCase(action)) {
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");

                request.setAttribute("filterStartDate", startDate);
                request.setAttribute("filterEndDate", endDate);

                List<Map<String, String>> leaseData = dao.getTenantLeaseReport(startDate, endDate);
                request.setAttribute("leaseData", leaseData);
                request.getRequestDispatcher("tenant_report.jsp").forward(request, response);
                
            // =========================================================
            // REPORT 4: PROSPECTIVE WORKFLOW APPLICATIONS LOG
            // =========================================================
            } else if ("application".equalsIgnoreCase(action)) {
                String statusFilter = request.getParameter("status");
                if (statusFilter == null || statusFilter.isEmpty()) {
                    statusFilter = "ALL";
                }
                
                request.setAttribute("filterStatus", statusFilter);
                
                List<Map<String, String>> appData = dao.getApplicationReport(statusFilter);
                request.setAttribute("appData", appData);
                request.getRequestDispatcher("application_report.jsp").forward(request, response);

            // =========================================================
            // EXPORT ENGINES: MANAGEMENT SUMMARY
            // =========================================================
            } else if ("exportSummaryCSV".equalsIgnoreCase(action)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"Management_Summary_" + LocalDate.now() + ".csv\"");
                Map<String, Integer> metrics = dao.getSummaryMetrics();

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("Metric,Total Count");
                    writer.println("Total Properties," + (metrics.containsKey("totalProperties") ? metrics.get("totalProperties") : 0));
                    writer.println("Active Leases," + (metrics.containsKey("activeLeases") ? metrics.get("activeLeases") : 0));
                    writer.println("Open Maintenance," + (metrics.containsKey("openMaintenance") ? metrics.get("openMaintenance") : 0));
                    writer.println("Pending Applications," + (metrics.containsKey("pendingApps") ? metrics.get("pendingApps") : 0));
                }
                return;

            } else if ("exportSummaryTXT".equalsIgnoreCase(action)) {
                response.setContentType("text/plain");
                response.setHeader("Content-Disposition", "attachment; filename=\"Management_Summary_" + LocalDate.now() + ".txt\"");
                Map<String, Integer> metrics = dao.getSummaryMetrics();

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("====================================================");
                    writer.println("        EXECUTIVE MANAGEMENT SUMMARY                ");
                    writer.println("        Generated on: " + LocalDate.now());
                    writer.println("====================================================");
                    writer.println("Total Properties     : " + (metrics.containsKey("totalProperties") ? metrics.get("totalProperties") : 0));
                    writer.println("Active Leases        : " + (metrics.containsKey("activeLeases") ? metrics.get("activeLeases") : 0));
                    writer.println("Open Maintenance     : " + (metrics.containsKey("openMaintenance") ? metrics.get("openMaintenance") : 0));
                    writer.println("Pending Applications : " + (metrics.containsKey("pendingApps") ? metrics.get("pendingApps") : 0));
                    writer.println("====================================================");
                }
                return;

            } else if ("exportSummaryPDF".equalsIgnoreCase(action)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"Management_Summary_" + LocalDate.now() + ".pdf\"");
                Map<String, Integer> metrics = dao.getSummaryMetrics();

                int totalProperties = (metrics != null && metrics.containsKey("totalProperties")) ? metrics.get("totalProperties") : 0;
                int activeLeases = (metrics != null && metrics.containsKey("activeLeases")) ? metrics.get("activeLeases") : 0;
                int openMaintenance = (metrics != null && metrics.containsKey("openMaintenance")) ? metrics.get("openMaintenance") : 0;
                int pendingApps = (metrics != null && metrics.containsKey("pendingApps")) ? metrics.get("pendingApps") : 0;

                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open(); 
                    
                    Paragraph title = new Paragraph("Executive Management Summary", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    title.setSpacingAfter(20f);
                    document.add(title); 

                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(80); 
                    
                    String[] headers = {"Operational Metric", "Total Count"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                        cell.setBackgroundColor(new Color(230, 230, 230));
                        cell.setPadding(8f);
                        table.addCell(cell);
                    }
                    
                    Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                    
                    table.addCell(new Phrase("Total Properties", rowFont));
                    table.addCell(new Phrase(String.valueOf(totalProperties), rowFont));
                    table.addCell(new Phrase("Active Leases", rowFont));
                    table.addCell(new Phrase(String.valueOf(activeLeases), rowFont));
                    table.addCell(new Phrase("Open Maintenance", rowFont));
                    table.addCell(new Phrase(String.valueOf(openMaintenance), rowFont));
                    table.addCell(new Phrase("Pending Applications", rowFont));
                    table.addCell(new Phrase(String.valueOf(pendingApps), rowFont));

                    document.add(table);
                    document.close();
                } catch (DocumentException e) { throw new ServletException(e); }
                return;

            // =========================================================
            // EXPORT ENGINES: MAINTENANCE
            // =========================================================
            } else if ("exportCSV".equalsIgnoreCase(action)) {
                response.setContentType("text/csv");
                String fileName = "Maintenance_Report_" + LocalDate.now() + ".csv";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                String priorityFilter = request.getParameter("priority") != null ? request.getParameter("priority") : "ALL";
                List<Map<String, String>> csvData = dao.getMaintenanceReport(sessionUser != null ? sessionUser : 0, (String) request.getSession().getAttribute("role"), statusFilter, priorityFilter);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("Property Address,Tenant Name,Description,Priority,Status,Cost");
                    for (Map<String, String> row : csvData) {
                        writer.println("\"" + row.get("address") + "\",\"" + row.get("tenantName") + "\",\"" + row.get("description") + "\",\"" + row.get("priority") + "\",\"" + row.get("status") + "\"," + row.get("cost"));
                    }
                }
                dao.addReport(new Report(0, "Maintenance Report (CSV)", generatedBy, logDate, "exports/" + fileName));
                return;

            } else if ("exportTXT".equalsIgnoreCase(action)) {
                response.setContentType("text/plain");
                String fileName = "Maintenance_Audit_" + LocalDate.now() + ".txt";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                String priorityFilter = request.getParameter("priority") != null ? request.getParameter("priority") : "ALL";
                List<Map<String, String>> txtData = dao.getMaintenanceReport(sessionUser != null ? sessionUser : 0, (String) request.getSession().getAttribute("role"), statusFilter, priorityFilter);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("====================================================");
                    writer.println("        REAL ESTATE MAINTENANCE AUDIT SUMMARY       ");
                    writer.println("====================================================");
                    for (Map<String, String> row : txtData) {
                        writer.println("Property Location : " + row.get("address"));
                        writer.println("Tenant Name       : " + row.get("tenantName"));
                        writer.println("Priority & Status : " + row.get("priority") + " | " + row.get("status"));
                        writer.println("Cost              : R " + row.get("cost"));
                        writer.println("----------------------------------------------------");
                    }
                }
                dao.addReport(new Report(0, "Maintenance Report (TXT)", generatedBy, logDate, "exports/" + fileName));
                return;

            } else if ("exportPDF".equalsIgnoreCase(action)) {
                response.setContentType("application/pdf");
                String fileName = "Maintenance_Report_" + LocalDate.now() + ".pdf";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                String priorityFilter = request.getParameter("priority") != null ? request.getParameter("priority") : "ALL";
                List<Map<String, String>> pdfData = dao.getMaintenanceReport(sessionUser != null ? sessionUser : 0, (String) request.getSession().getAttribute("role"), statusFilter, priorityFilter);

                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open(); 
                    
                    Paragraph title = new Paragraph("Maintenance Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    title.setSpacingAfter(20f);
                    document.add(title); 

                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100); 
                    String[] headers = {"Property", "Tenant", "Description", "Priority", "Status", "Cost"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                        cell.setBackgroundColor(new Color(230, 230, 230));
                        table.addCell(cell);
                    }
                    Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                    for (Map<String, String> row : pdfData) {
                        table.addCell(new Phrase(row.get("address"), rowFont));
                        table.addCell(new Phrase(row.get("tenantName"), rowFont));
                        table.addCell(new Phrase(row.get("description"), rowFont));
                        table.addCell(new Phrase(row.get("priority"), rowFont));
                        table.addCell(new Phrase(row.get("status"), rowFont));
                        table.addCell(new Phrase(row.get("cost"), rowFont));
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException e) { throw new ServletException(e); }
                
                dao.addReport(new Report(0, "Maintenance Report (PDF)", generatedBy, logDate, "exports/" + fileName));
                return;

            // =========================================================
            // EXPORT ENGINES: ACTIVE LEASES
            // =========================================================
            } else if ("exportLeasesCSV".equalsIgnoreCase(action)) {
                response.setContentType("text/csv");
                String fileName = "Active_Leases_" + LocalDate.now() + ".csv";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                List<Map<String, String>> leaseData = dao.getTenantLeaseReport(startDate, endDate);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("Tenant Name,Property Address,Lease Start Date,Lease End Date");
                    for (Map<String, String> row : leaseData) {
                        writer.println("\"" + row.get("tenantName") + "\",\"" + row.get("address") + "\",\"" + row.get("leaseStart") + "\",\"" + row.get("leaseEnd") + "\"");
                    }
                }
                dao.addReport(new Report(0, "Tenant Report (CSV)", generatedBy, logDate, "exports/" + fileName));
                return;

            } else if ("exportLeasesTXT".equalsIgnoreCase(action)) {
                response.setContentType("text/plain");
                String fileName = "Active_Leases_" + LocalDate.now() + ".txt";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                List<Map<String, String>> leaseData = dao.getTenantLeaseReport(startDate, endDate);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("====================================================");
                    writer.println("        SYSTEM MASTER ACTIVE LEASE ACCOUNT RECORDS  ");
                    writer.println("====================================================");
                    if (startDate != null && !startDate.isEmpty()) {
                        writer.println("Filtered From: " + startDate + " To: " + endDate);
                        writer.println("----------------------------------------------------");
                    }
                    for (Map<String, String> row : leaseData) {
                        writer.println("Tenant   : " + row.get("tenantName"));
                        writer.println("Property : " + row.get("address"));
                        writer.println("Lease    : " + row.get("leaseStart") + " to " + row.get("leaseEnd"));
                        writer.println("----------------------------------------------------");
                    }
                }
                dao.addReport(new Report(0, "Tenant Report (TXT)", generatedBy, logDate, "exports/" + fileName));
                return;

            } else if ("exportLeasesPDF".equalsIgnoreCase(action)) {
                response.setContentType("application/pdf");
                String fileName = "Active_Leases_" + LocalDate.now() + ".pdf";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                List<Map<String, String>> leaseData = dao.getTenantLeaseReport(startDate, endDate);

                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open(); 
                    
                    Paragraph title = new Paragraph("Active Leases Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    title.setSpacingAfter(10f);
                    document.add(title); 
                    
                    if (startDate != null && !startDate.isEmpty()) {
                        Paragraph subTitle = new Paragraph("Date Range: " + startDate + " to " + endDate, FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY));
                        subTitle.setAlignment(Paragraph.ALIGN_CENTER);
                        subTitle.setSpacingAfter(20f);
                        document.add(subTitle);
                    } else {
                        title.setSpacingAfter(20f);
                    }

                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100); 
                    String[] headers = {"Tenant Name", "Property Address", "Start Date", "End Date"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                        cell.setBackgroundColor(new Color(230, 230, 230));
                        table.addCell(cell);
                    }
                    Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                    for (Map<String, String> row : leaseData) {
                        table.addCell(new Phrase(row.get("tenantName"), rowFont));
                        table.addCell(new Phrase(row.get("address"), rowFont));
                        table.addCell(new Phrase(row.get("leaseStart"), rowFont));
                        table.addCell(new Phrase(row.get("leaseEnd"), rowFont));
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException e) { throw new ServletException(e); }
                
                dao.addReport(new Report(0, "Tenant Report (PDF)", generatedBy, logDate, "exports/" + fileName));
                return;

            // =========================================================
            // EXPORT ENGINES: PROPERTY APPLICATIONS
            // =========================================================
            } else if ("exportAppsCSV".equalsIgnoreCase(action)) {
                response.setContentType("text/csv");
                String fileName = "Applications_" + LocalDate.now() + ".csv";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                List<Map<String, String>> appData = dao.getApplicationReport(statusFilter);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("Applicant Name,Property Location,Application Date,Status");
                    for (Map<String, String> row : appData) {
                        writer.println("\"" + row.get("applicantName") + "\",\"" + row.get("address") + "\",\"" + row.get("date") + "\",\"" + row.get("status") + "\"");
                    }
                }
                dao.addReport(new Report(0, "Property Report (CSV)", generatedBy, logDate, "exports/" + fileName));
                return;

            } else if ("exportAppsTXT".equalsIgnoreCase(action)) {
                response.setContentType("text/plain");
                String fileName = "Applications_" + LocalDate.now() + ".txt";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                List<Map<String, String>> appData = dao.getApplicationReport(statusFilter);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println("====================================================");
                    writer.println("        REAL ESTATE PROPERTY APPLICATIONS LOG       ");
                    writer.println("====================================================");
                    for (Map<String, String> row : appData) {
                        writer.println("Applicant : " + row.get("applicantName"));
                        writer.println("Property  : " + row.get("address"));
                        writer.println("Date      : " + row.get("date") + " | Status: " + row.get("status"));
                        writer.println("----------------------------------------------------");
                    }
                }
                dao.addReport(new Report(0, "Property Report (TXT)", generatedBy, logDate, "exports/" + fileName));
                return;
                
            } else if ("exportAppsPDF".equalsIgnoreCase(action)) {
                response.setContentType("application/pdf");
                String fileName = "Applications_" + LocalDate.now() + ".pdf";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                
                String statusFilter = request.getParameter("status") != null ? request.getParameter("status") : "ALL";
                List<Map<String, String>> appData = dao.getApplicationReport(statusFilter);

                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open(); 
                    
                    Paragraph title = new Paragraph("Property Applications Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    title.setSpacingAfter(20f);
                    document.add(title); 

                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100); 
                    String[] headers = {"Applicant Name", "Property Location", "Application Date", "Status"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                        cell.setBackgroundColor(new Color(230, 230, 230));
                        table.addCell(cell);
                    }
                    Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                    for (Map<String, String> row : appData) {
                        table.addCell(new Phrase(row.get("applicantName"), rowFont));
                        table.addCell(new Phrase(row.get("address"), rowFont));
                        table.addCell(new Phrase(row.get("date"), rowFont));
                        table.addCell(new Phrase(row.get("status"), rowFont));
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException e) { throw new ServletException(e); }
                
                dao.addReport(new Report(0, "Property Report (PDF)", generatedBy, logDate, "exports/" + fileName));
                return;

            // =========================================================
            // HTML FORM ROUTING: LOADS ALL RECORDS TO THE TABLE VIEW
            // =========================================================
            } else if ("generateChosen".equalsIgnoreCase(action)) {
                List<Report> reports = dao.getAllReports();
                request.setAttribute("reports", reports);
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
                return;

            // =========================================================
            // CORE ENTITY MANAGEMENT CRUD OPERATIONS
            // =========================================================
            } else if (action == null || "list".equalsIgnoreCase(action)) {
                List<Report> reports = dao.getAllReports();
                request.setAttribute("reports", reports);
                request.getRequestDispatcher("Reports.jsp").forward(request, response);

            } else if ("edit".equalsIgnoreCase(action)) {
                int reportID = Integer.parseInt(request.getParameter("reportID"));
                Report report = dao.getReportById(reportID);
                request.setAttribute("reportObj", report);
                request.getRequestDispatcher("EditReport.jsp").forward(request, response);

            } else if ("delete".equalsIgnoreCase(action)) {
                // FIXED: Changed sendRedirect container routing pattern to explicit structural request forward
                int reportID = Integer.parseInt(request.getParameter("reportID"));
                dao.removeReport(reportID);
                
                List<Report> reports = dao.getAllReports();
                request.setAttribute("reports", reports);
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
                return;

            } else if ("download".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("reportID"));
                Report report = dao.getReportById(id);

                if (report != null) {
                    String absolutePath = getServletContext().getRealPath(report.getFilePath());
                    File file = new File(absolutePath);

                    if (file.exists()) {
                        response.setContentType("application/octet-stream");
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

                        try (FileInputStream in = new FileInputStream(file);
                             OutputStream out = response.getOutputStream()) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                        }
                        return; 
                    } else {
                        request.setAttribute("errorMessage", "File not found on server: " + report.getFilePath());
                        request.setAttribute("reports", dao.getAllReports());
                        request.getRequestDispatcher("Reports.jsp").forward(request, response);
                    }
                }
            } else {
                request.setAttribute("errorMessage", "Unknown action command.");
                request.setAttribute("reports", dao.getAllReports());
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "System Error: " + e.getMessage());
            try {
                request.setAttribute("reports", dao.getAllReports());
            } catch (SQLException ex) { }
            request.getRequestDispatcher("Reports.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        ReportDAO reportDAO = new ReportDAO(); 

        String type = request.getParameter("type");
        String dateStr = request.getParameter("date");
        String filePath = request.getParameter("filePath");
        String generatedByStr = request.getParameter("generatedBy");

        try {
            if (type == null || type.trim().isEmpty() || dateStr == null || dateStr.trim().isEmpty() || 
                filePath == null || filePath.trim().isEmpty() || generatedByStr == null || generatedByStr.trim().isEmpty()) {
                
                request.setAttribute("errorMessage", "Validation Error: All inputs are mandatory. Form fields cannot be empty.");
                request.setAttribute("reports", reportDAO.getAllReports());
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
                return;
            }

            if (type.length() > 50 || filePath.length() > 255) {
                request.setAttribute("errorMessage", "Validation Error: Input length limits exceeded. Type (max 50), Path (max 255).");
                request.setAttribute("reports", reportDAO.getAllReports()); 
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
                return;
            }

            int reportID = request.getParameter("reportID") != null && !request.getParameter("reportID").isEmpty()
                    ? Integer.parseInt(request.getParameter("reportID")) : 0;
            
            int generatedBy = 0;
            try {
                 generatedBy = Integer.parseInt(generatedByStr);   
                 if (generatedBy <= 0) throw new NumberFormatException();
            } catch(NumberFormatException e) {
                 request.setAttribute("errorMessage", "Validation Error: User ID value must be a valid positive integer number.");
                 request.setAttribute("reports", reportDAO.getAllReports()); 
                 request.getRequestDispatcher("Reports.jsp").forward(request, response);
                 return;
            }
            
            java.sql.Date date = null;
            try {
                date = java.sql.Date.valueOf(dateStr); 
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Validation Error: Invalid date sequence format. Please format using YYYY-MM-DD.");
                request.setAttribute("reports", reportDAO.getAllReports()); 
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
                return;
            }

            Report report = new Report(reportID, type, generatedBy, date, filePath);

            if ("add".equalsIgnoreCase(action)) {
                reportDAO.addReport(report);
                response.sendRedirect("ReportServlet?action=list");
            } else if ("update".equalsIgnoreCase(action)) {
                reportDAO.updateReport(report);
                response.sendRedirect("ReportServlet?action=list");
            } else {
                request.setAttribute("errorMessage", "Unknown action: " + action);
                request.setAttribute("reports", reportDAO.getAllReports());
                request.getRequestDispatcher("Reports.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Database transaction aborted: " + e.getMessage());
            try { request.setAttribute("reports", reportDAO.getAllReports()); } catch (Exception ignored) {}
            request.getRequestDispatcher("Reports.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Report Servlet for handling report generation and downloads.";
    }
}