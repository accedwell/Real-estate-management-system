package ac.za.tut.model;

import java.sql.Date;

public class Transaction {
    private int transactionID;
    private int propertyID;
    private int buyerID;
    private int sellerID;
    private double amount;
    private Date date;
    private String type;
    private String paymentMethod;
    private String contractDetails;

    // Constructor with all fields
    public Transaction(int transactionID, int propertyID, int buyerID, int sellerID,
                       double amount, Date date, String type, String paymentMethod, String contractDetails) {
        this.transactionID = transactionID;
        this.propertyID = propertyID;
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.contractDetails = contractDetails;
    }

    // Empty constructor
    public Transaction() {}

    // Getters and Setters
    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getPropertyID() {
        return propertyID;
    }
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public int getBuyerID() {
        return buyerID;
    }
    public void setBuyerID(int buyerID) {
        this.buyerID = buyerID;
    }

    public int getSellerID() {
        return sellerID;
    }
    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getContractDetails() {
        return contractDetails;
    }
    public void setContractDetails(String contractDetails) {
        this.contractDetails = contractDetails;
    }
}
