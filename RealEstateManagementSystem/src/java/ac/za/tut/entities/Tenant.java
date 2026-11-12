package ac.za.tut.entities;

import java.sql.Date;

public class Tenant {
    private int tenantID;
    private int userID;
    private int propertyID;
    private Date leaseStart;
    private Date leaseEnd;

    // Constructor with all fields
    public Tenant(int tenantID, int userID, int propertyID, Date leaseStart, Date leaseEnd) {
        this.tenantID = tenantID;
        this.userID = userID;
        this.propertyID = propertyID;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
    }

    // Empty constructor
    public Tenant() {}

    // Getters and Setters
    public int getTenantID() {
        return tenantID;
    }
    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPropertyID() {
        return propertyID;
    }
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public Date getLeaseStart() {
        return leaseStart;
    }
    public void setLeaseStart(Date leaseStart) {
        this.leaseStart = leaseStart;
    }

    public Date getLeaseEnd() {
        return leaseEnd;
    }
    public void setLeaseEnd(Date leaseEnd) {
        this.leaseEnd = leaseEnd;
    }
}
