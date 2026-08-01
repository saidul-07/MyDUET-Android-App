package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;

public class AdmissionResult {
    @SerializedName("roll")
    private int roll;
    @SerializedName("name")
    private String name;
    @SerializedName("fatherName")
    private String fatherName;
    @SerializedName("department")
    private String department;
    @SerializedName("status")
    private String status;
    @SerializedName("waitingMerit")
    private Integer waitingMerit;

    // Getters
    public int getRoll() { return roll; }
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public Integer getWaitingMerit() { return waitingMerit; }
}