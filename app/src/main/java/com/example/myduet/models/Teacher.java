package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;

public class Teacher {
    @SerializedName("name")
    private String name;

    @SerializedName("designation")
    private String designation;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("officeRoom")
    private String officeRoom;

    @SerializedName("researchInterests")
    private String researchInterests;

    @SerializedName("profile")
    private String profile;

    @SerializedName("image")
    private String image;

    // Getters
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getOfficeRoom() { return officeRoom; }
    public String getResearchInterests() { return researchInterests; }
    public String getProfile() { return profile; }
    public String getImage() { return image; }
}
