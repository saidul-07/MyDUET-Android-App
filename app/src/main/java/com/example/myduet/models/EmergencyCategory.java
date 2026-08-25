package com.example.myduet.models;

public class EmergencyCategory {
    private String id;
    private String name;
    private String description;
    private int iconRes;
    private String count;
    private int bgColor;
    private int iconTint;
    private String iconResName;

    public EmergencyCategory(String id, String name, String description, int iconRes, String count, int bgColor, int iconTint) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconRes = iconRes;
        this.count = count;
        this.bgColor = bgColor;
        this.iconTint = iconTint;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
    public String getIconResName() { return iconResName; }
    public String getCount() { return count; }
    public int getBgColor() { return bgColor; }
    public int getIconTint() { return iconTint; }
}