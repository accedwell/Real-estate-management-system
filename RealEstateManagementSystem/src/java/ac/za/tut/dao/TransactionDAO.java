package ac.za.tut.dao;

import ac.za.tut.model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TransactionDAO {

    public void addTransaction(Transaction tx) throws SQLException {
        String sql = "INSERT INTO Transactions (propertyID, buyerID, sellerID, amount, date, type, paymentMethod, contractDetails) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tx.getPropertyID());
            ps.setInt(2, tx.getBuyerID());
            ps.setInt(3, tx.getSellerID());
            ps.setDouble(4, tx.getAmount());
            ps.setDate(5, tx.getDate());
            ps.setString(6, tx.getType());
            ps.setString(7, tx.getPaymentMethod());
            ps.setString(8, tx.getContractDetails());
            ps.executeUpdate();
        }
    }

    public void updateTransaction(int transactionID, double amount, String status) throws SQLException {
        String sql = "UPDATE Transactions SET amount=?, type=? WHERE transactionID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, status);
            ps.setInt(3, transactionID);
            ps.executeUpdate();
        }
    }

    public void removeTransaction(int transactionID) throws SQLException {
        String sql = "DELETE FROM Transactions WHERE transactionID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionID);
            ps.executeUpdate();
        }
    }

    public Transaction getTransactionById(int transactionID) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE transactionID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Transaction(
                    rs.getInt("transactionID"),
                    rs.getInt("propertyID"),
                    rs.getInt("buyerID"),
                    rs.getInt("sellerID"),
                    rs.getDouble("amount"),
                    rs.getDate("date"),
                    rs.getString("type"),
                    rs.getString("paymentMethod"),
                    rs.getString("contractDetails")
                );
            }
        }
        return null;
    }
     public void updateTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE Transactions SET propertyID=?, buyerID=?, sellerID=?, amount=?, date=?, type=?, paymentMethod=?, contractDetails=? WHERE transactionID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaction.getPropertyID());
            ps.setInt(2, transaction.getBuyerID());
            ps.setInt(3, transaction.getSellerID());
            ps.setDouble(4, transaction.getAmount());
            ps.setDate(5, transaction.getDate());
            ps.setString(6, transaction.getType());
            ps.setString(7, transaction.getPaymentMethod());
            ps.setString(8, transaction.getContractDetails());
            ps.setInt(9, transaction.getTransactionID());
            ps.executeUpdate();
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("transactionID"),
                    rs.getInt("propertyID"),
                    rs.getInt("buyerID"),
                    rs.getInt("sellerID"),
                    rs.getDouble("amount"),
                    rs.getDate("date"),
                    rs.getString("type"),
                    rs.getString("paymentMethod"),
                    rs.getString("contractDetails")
                ));
            }
        }
        return transactions;
    }
}
