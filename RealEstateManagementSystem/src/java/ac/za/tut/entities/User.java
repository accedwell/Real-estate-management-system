package ac.za.tut.entities;

public class User {
    private int userID;
    private String name;
    private String role;
    private String email;
    private String phone;
    private byte[] passwordHash;

    // Constructor with all fields
    public User(int userID, String name, String role, String email, String phone, byte[] passwordHash) {
        this.userID = userID;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
    }

    // Empty constructor
    public User() {}

    // Getters and Setters
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }
}
