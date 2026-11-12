package ac.za.tut.entities;



public class Property {
    private int propertyID;
    private int ownerID;
    private String ownerName; // New field to hold the name from the Users table
    private String address;
    private String type;
    private int size;
    private int rooms;
    private String status;

    // Updated constructor with ownerName
    public Property(int propertyID, int ownerID, String ownerName, String address, String type, int size, int rooms, String status) {
        this.propertyID = propertyID;
        this.ownerID = ownerID;
        this.ownerName = ownerName;
        this.address = address;
        this.type = type;
        this.size = size;
        this.rooms = rooms;
        this.status = status;
    }
    public Property(int propertyID, int ownerID, String address, String type, int size, int rooms, String status) {
        this.propertyID = propertyID;
        this.ownerID = ownerID;
        this.address = address;
        this.type = type;
        this.size = size;
        this.rooms = rooms;
        this.status = status;
    }
    // Empty constructor
    public Property() {}

    // Getters and Setters
    public int getPropertyID() {
        return propertyID;
    }
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    public int getOwnerID() {
        return ownerID;
    }
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    // New Getter and Setter for ownerName
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public int getRooms() {
        return rooms;
    }
    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}