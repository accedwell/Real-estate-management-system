package ac.za.tut.entities;

import java.sql.Date;

public class Application {
    private int applicationID;
    private int tenantID;
    private int propertyID;
    private Date date;
    private String status;

    // Constructor with all fields
    public Application(int applicationID, int tenantID, int propertyID, Date date, String status) {
        this.applicationID = applicationID;
        this.tenantID = tenantID;
        this.propertyID = propertyID;
        this.date = date;
        this.status = status;
    }

    // Empty constructor
    public Application() {}

    // Getters and Setters
    public int getApplicationID() {
        return applicationID;
    }
    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public int getTenantID() {
        return tenantID;
    }
    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getPropertyID() {
        return propertyID;
    }
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
