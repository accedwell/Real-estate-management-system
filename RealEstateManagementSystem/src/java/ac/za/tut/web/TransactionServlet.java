/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package ac.za.tut.web;

import ac.za.tut.dao.TransactionDAO;
import ac.za.tut.model.Transaction;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mandla
 */
public class TransactionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TransactionServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TransactionServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String action = request.getParameter("action"); // e.g. "add" or "update"

    int transactionID = request.getParameter("transactionID") != null
            ? Integer.parseInt(request.getParameter("transactionID"))
            : 0;
    int propertyID = Integer.parseInt(request.getParameter("propertyID"));
    int buyerID = Integer.parseInt(request.getParameter("buyerID"));
    int sellerID = Integer.parseInt(request.getParameter("sellerID"));
    double amount = Double.parseDouble(request.getParameter("amount"));
    java.sql.Date date = java.sql.Date.valueOf(request.getParameter("date"));
    String type = request.getParameter("type");
    String paymentMethod = request.getParameter("paymentMethod");
    String contractDetails = request.getParameter("contractDetails");

    Transaction transaction = new Transaction(transactionID, propertyID, buyerID, sellerID,
                                              amount, date, type, paymentMethod, contractDetails);

    try {
        TransactionDAO transactionDAO = new TransactionDAO();

        if ("add".equalsIgnoreCase(action)) {
            transactionDAO.addTransaction(transaction);
            response.sendRedirect("TransactionList.jsp?success=Transaction recorded");
        } else if ("update".equalsIgnoreCase(action)) {
            transactionDAO.updateTransaction(transaction);
            response.sendRedirect("TransactionList.jsp?success=Transaction updated");
        } else {
            response.sendRedirect("TransactionList.jsp?error=Unknown action");
        }
    } catch (Exception e) {
        throw new ServletException("Transaction operation failed", e);
    }
}


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
