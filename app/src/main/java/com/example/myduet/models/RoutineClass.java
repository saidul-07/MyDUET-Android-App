package com.example.myduet.models;

public class RoutineClass {
    private String courseCode;
    private String courseName;
    private String type; // Theory or Lab
    private String time;
    private String room;

    public RoutineClass(String courseCode, String courseName, String type, String time, String room) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.type = type;
        this.time = time;
        this.room = room;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getType() { return type; }
    public String getTime() { return time; }
    public String getRoom() { return room; }

    public boolean isLab() {
        return "Lab".equalsIgnoreCase(type);
    }
}