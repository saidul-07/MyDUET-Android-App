package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Profile implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("role")
    private String role; // "ADMIN", "UNIVERSITY_AUTHORITY", "CLUB_AUTHORITY"

    @SerializedName("display_name")
    private String displayName;

    public Profile() {}

    public Profile(String id, String email, String userId, String role, String displayName) {
        this.id = id;
        this.email = email;
        this.userId = userId;
        this.role = role;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

