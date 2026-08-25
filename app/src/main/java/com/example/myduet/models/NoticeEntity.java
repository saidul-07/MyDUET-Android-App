package com.example.myduet.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notices")
public class NoticeEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String description;
    private String pdfUrl;
    private String publishDate;
    private String category;
    private String thumbnail;
    private long lastUpdated;
    private String sourceUrl;
    private int originalIndex;

    public NoticeEntity(@NonNull String id, String title, String description, String pdfUrl, 
                        String publishDate, String category, String thumbnail, long lastUpdated, 
                        String sourceUrl, int originalIndex) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pdfUrl = pdfUrl;
        this.publishDate = publishDate;
        this.category = category;
        this.thumbnail = thumbnail;
        this.lastUpdated = lastUpdated;
        this.sourceUrl = sourceUrl;
        this.originalIndex = originalIndex;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public int getOriginalIndex() { return originalIndex; }
    public void setOriginalIndex(int originalIndex) { this.originalIndex = originalIndex; }
}
