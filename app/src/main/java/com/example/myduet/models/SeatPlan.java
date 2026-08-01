package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;

public class SeatPlan {
    @SerializedName("startRoll")
    private int startRoll;
    @SerializedName("endRoll")
    private int endRoll;
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

    // Getters
    public int getStartRoll() { return startRoll; }
    public int getEndRoll() { return endRoll; }
    public String getBuilding() { return building; }
    public String getRoom() { return room; }
    public String getDepartment() { return department; }
    public String getExamDate() { return examDate; }
    public String getShift() { return shift; }

    // Check if roll belongs to this range
    public boolean isInRange(int roll) {
        return roll >= startRoll && roll <= endRoll;
    }
}