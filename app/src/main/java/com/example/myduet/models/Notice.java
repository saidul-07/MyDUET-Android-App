package com.example.myduet.models;

public class Notice {
    private String id;
    private String category;
    private String title;
    private String description;
    private String date;
    private String url;
    private int originalIndex; // Added to maintain site sequence for same-day notices

    public Notice(String id, String category, String title, String description, String date, String url, int originalIndex) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.date = date;
        this.url = url;
        this.originalIndex = originalIndex;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getUrl() { return url; }
    public int getOriginalIndex() { return originalIndex; }
}