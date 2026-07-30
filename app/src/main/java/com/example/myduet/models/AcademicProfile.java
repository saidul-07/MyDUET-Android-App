package com.example.myduet.models;

public class AcademicProfile {
    private String department;
    private String year;
    private String section;

    public AcademicProfile(String department, String year, String section) {
        this.department = department;
        this.year = year;
        this.section = section;
    }

    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public String getSection() { return section; }
}