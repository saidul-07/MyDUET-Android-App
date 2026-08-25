package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;

public class SeatPlan {
    @SerializedName("roll")
    private int roll;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("fatherName")
    private String fatherName;
    
    @SerializedName("building")
    private String building;
    
    @SerializedName("room")
    private String room;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("examDate")
    private String examDate;
    
    @SerializedName("shift")
    private String shift;

    // Helper fields for UI display compatibility
    private String candidateName;
    private String candidateFatherName;
    private int searchedRoll;

    // Getters and Setters
    public int getRoll() { return roll; }
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getBuilding() { return building; }
    public String getRoom() { return room; }
    public String getDepartment() { return department; }
    public String getExamDate() { return examDate; }
    public String getShift() { return shift; }

    public String getCandidateName() { 
        return (candidateName != null) ? candidateName : name; 
    }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateFatherName() { 
        return (candidateFatherName != null) ? candidateFatherName : fatherName; 
    }
    public void setCandidateFatherName(String candidateFatherName) { this.candidateFatherName = candidateFatherName; }

    public int getSearchedRoll() { 
        return (searchedRoll != 0) ? searchedRoll : roll; 
    }
    public void setSearchedRoll(int searchedRoll) { this.searchedRoll = searchedRoll; }
}