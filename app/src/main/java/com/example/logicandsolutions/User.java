package com.example.logicandsolutions;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String userType;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String phone, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}