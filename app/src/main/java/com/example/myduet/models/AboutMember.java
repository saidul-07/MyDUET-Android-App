package com.example.myduet.models;

import java.io.Serializable;

public class AboutMember implements Serializable {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_MEMBER = 1;

    private int itemType = TYPE_MEMBER;
    private String year;
    private String name;
    private String appRole;
    private String series;
    private String currentRole;
    private String company;
    private String imageUrl;

    public AboutMember(String year) {
        this.itemType = TYPE_HEADER;
        this.year = year;
    }

    public AboutMember(String year, String name, String appRole, String series, String imageUrl) {
        this.itemType = TYPE_MEMBER;
        this.year = year;
        this.name = name;
        this.appRole = appRole;
        this.series = series;
        this.imageUrl = imageUrl;
    }

    public AboutMember(String year, String name, String appRole, String series, String currentRole, String company, String imageUrl) {
        this.itemType = TYPE_MEMBER;
        this.year = year;
        this.name = name;
        this.appRole = appRole;
        this.series = series;
        this.currentRole = currentRole;
        this.company = company;
        this.imageUrl = imageUrl;
    }

    public int getItemType() {
        return itemType;
    }

    public String getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public String getAppRole() {
        return appRole;
    }

    public String getSeries() {
        return series;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public String getCompany() {
        return company;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean hasCurrentWork() {
        return currentRole != null && !currentRole.trim().isEmpty();
    }
}