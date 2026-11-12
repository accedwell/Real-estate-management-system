package ac.za.tut.model;

public class MaintenanceRequest {
    private int requestID;
    private int tenantID;
    private int propertyID;
    private String description;
    private String priority;
    private String status;
    private double cost;
    private String resolutionNotes;

    // Constructor with all fields
    public MaintenanceRequest(int requestID, int tenantID, int propertyID,
                              String description, String priority, String status,
                              double cost, String resolutionNotes) {
        this.requestID = requestID;
        this.tenantID = tenantID;
        this.propertyID = propertyID;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.cost = cost;
        this.resolutionNotes = resolutionNotes;
    }

    // Empty constructor
    public MaintenanceRequest() {}

    // Getters and Setters
    public int getRequestID() {
        return requestID;
    }
    public void setRequestID(int requestID) {
        this.requestID = requestID;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
