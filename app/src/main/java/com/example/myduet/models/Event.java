package com.example.myduet.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Event implements Serializable {

    @SerializedName("id")
    private int eventId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type; // "University" or "Club"

    @SerializedName("club_name")
    private String clubName;

    @SerializedName("organizer_name")
    private String organizerName;

    @SerializedName("banner_url")
    private String bannerUrl;

    @SerializedName("event_date")
    private String eventDate; // YYYY-MM-DD

    @SerializedName("start_time")
    private String startTime; // HH:MM

    @SerializedName("end_time")
    private String endTime; // HH:MM

    @SerializedName("venue")
    private String venue;

    @SerializedName("registration_required")
    private boolean registrationRequired;

    @SerializedName("registration_deadline")
    private String registrationDeadline; // YYYY-MM-DD HH:MM

    @SerializedName("registration_url")
    private String registrationUrl;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("contact_email")
    private String contactEmail;

    @SerializedName("contact_phone")
    private String contactPhone;
    

    // Optional
    @SerializedName("max_participants")
    private Integer maxParticipants;

    @SerializedName("social_media_url")
    private String socialMediaUrl;

    @SerializedName("additional_info")
    private String additionalInfo;

    // Control fields
    @SerializedName("status")
    private String status; // "Upcoming", "Ongoing", "Completed", "Cancelled"

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_at")
    private long createdAt;

    @SerializedName("updated_at")
    private long updatedAt;

    public Event() {}

    // Getters and Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public boolean isRegistrationRequired() { return registrationRequired; }
    public void setRegistrationRequired(boolean registrationRequired) { this.registrationRequired = registrationRequired; }

    public String getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(String registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getSocialMediaUrl() { return socialMediaUrl; }
    public void setSocialMediaUrl(String socialMediaUrl) { this.socialMediaUrl = socialMediaUrl; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
