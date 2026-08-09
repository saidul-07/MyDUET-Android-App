package com.example.myduet.models;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String role; // "ADMIN", "UNIVERSITY_AUTHORITY", "CLUB_AUTHORITY"
    private String name;

    public User() {}

    public User(String userId, String role, String name) {
        this.userId = userId;
        this.role = role;
        this.name = name;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
