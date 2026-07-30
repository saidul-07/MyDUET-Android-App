package com.example.myduet.models;

import java.util.List;
import java.util.Map;

public class RoutineData {
    private String department;
    private String year;
    private String section;
    private Map<String, List<RoutineClass>> days;

    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public String getSection() { return section; }
    public Map<String, List<RoutineClass>> getDays() { return days; }
}