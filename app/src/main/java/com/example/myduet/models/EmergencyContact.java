package com.example.myduet.models;

public class EmergencyContact {
    private String id;
    private String name;
    private String personName;
    private String assistantName;
    private String phone;
    private String email;
    private String location;
    private String hours;
    private int iconResId;
    private String iconResName;

    public EmergencyContact(String id, String name, String personName, String assistantName, 
                            String phone, String email, String location, String hours) {
        this.id = id;
        this.name = name;
        this.personName = personName;
        this.assistantName = assistantName;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.hours = hours;
        this.iconResId = 0;
    }

    public EmergencyContact(String id, String name, String personName, String assistantName, 
                            String phone, String email, String location, String hours, int iconResId) {
        this(id, name, personName, assistantName, phone, email, location, hours);
        this.iconResId = iconResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPersonName() { return personName; }
    public String getAssistantName() { return assistantName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getLocation() { return location; }
    public String getHours() { return hours; }
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public String getIconResName() { return iconResName; }
}