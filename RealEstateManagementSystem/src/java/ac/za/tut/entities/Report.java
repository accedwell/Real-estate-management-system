package ac.za.tut.entities;

import java.sql.Date;

public class Report {
    private int reportID;
    private String type;
    private int generatedBy;   // FIXED: int to match schema
    private Date date;
    private String filePath;

    // Constructor with all fields
    public Report(int reportID, String type, int generatedBy, Date date, String filePath) {
        this.reportID = reportID;
        this.type = type;
        this.generatedBy = generatedBy;
        this.date = date;
        this.filePath = filePath;
    }

    // Empty constructor
    public Report() {}

    // Getters and Setters
    public int getReportID() { return reportID; }
    public void setReportID(int reportID) { this.reportID = reportID; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(int generatedBy) { this.generatedBy = generatedBy; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
