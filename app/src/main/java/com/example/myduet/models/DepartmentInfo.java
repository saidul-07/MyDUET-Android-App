package com.example.myduet.models;

public class DepartmentInfo {
    private String key;
    private String name;
    private int accentColorRes;
    private int iconRes;
    private int teacherCount;

    public DepartmentInfo(String key, String name, int accentColorRes, int iconRes, int teacherCount) {
        this.key = key;
        this.name = name;
        this.accentColorRes = accentColorRes;
        this.iconRes = iconRes;
        this.teacherCount = teacherCount;
    }

    public String getKey() { return key; }
    public String getName() { return name; }
    public int getAccentColorRes() { return accentColorRes; }
    public int getIconRes() { return iconRes; }
    public int getTeacherCount() { return teacherCount; }
    public void setTeacherCount(int count) { this.teacherCount = count; }
}
