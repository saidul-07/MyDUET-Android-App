package com.example.myduet.models;

public class LibraryMenuItem {
    private String id;
    private String title;
    private String description;
    private int iconRes;

    public LibraryMenuItem(String id, String title, String description, int iconRes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconRes() { return iconRes; }
}