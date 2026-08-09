package com.example.myduet.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.myduet.models.Event;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EventDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myduet_events.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_USERS = "users";

    // Common columns
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    // Events Table Columns
    public static final String KEY_EVENT_ID = "eventId";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TYPE = "type"; // University / Club
    public static final String KEY_CLUB_NAME = "clubName";
    public static final String KEY_ORGANIZER_NAME = "organizerName";
    public static final String KEY_BANNER_URL = "bannerUrl";
    public static final String KEY_EVENT_DATE = "eventDate"; // YYYY-MM-DD
    public static final String KEY_START_TIME = "startTime"; // HH:MM
    public static final String KEY_END_TIME = "endTime"; // HH:MM
    public static final String KEY_VENUE = "venue";
    public static final String KEY_REG_REQUIRED = "registrationRequired"; // 0 or 1
    public static final String KEY_REG_DEADLINE = "registrationDeadline"; // YYYY-MM-DD HH:MM
    public static final String KEY_REG_URL = "registrationUrl";
    public static final String KEY_CONTACT_NAME = "contactName";
    public static final String KEY_CONTACT_EMAIL = "contactEmail";
    public static final String KEY_CONTACT_PHONE = "contactPhone";
    public static final String KEY_MAX_PARTICIPANTS = "maxParticipants";
    public static final String KEY_SOCIAL_MEDIA_URL = "socialMediaUrl";
    public static final String KEY_ADDITIONAL_INFO = "additionalInfo";
    public static final String KEY_STATUS = "status"; // Upcoming, Ongoing, Completed, Cancelled
    public static final String KEY_CREATED_BY = "createdBy";

    // Users Table Columns
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_PASSWORD_HASH = "passwordHash";
    public static final String KEY_ROLE = "role"; // ADMIN, UNIVERSITY_AUTHORITY, CLUB_AUTHORITY
    public static final String KEY_DISPLAY_NAME = "displayName";

    // Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_USER_ID + " TEXT PRIMARY KEY,"
            + KEY_PASSWORD_HASH + " TEXT,"
            + KEY_ROLE + " TEXT,"
            + KEY_DISPLAY_NAME + " TEXT"
            + ")";

    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_TYPE + " TEXT,"
            + KEY_CLUB_NAME + " TEXT,"
            + KEY_ORGANIZER_NAME + " TEXT,"
            + KEY_BANNER_URL + " TEXT,"
            + KEY_EVENT_DATE + " TEXT,"
            + KEY_START_TIME + " TEXT,"
            + KEY_END_TIME + " TEXT,"
            + KEY_VENUE + " TEXT,"
            + KEY_REG_REQUIRED + " INTEGER,"
            + KEY_REG_DEADLINE + " TEXT,"
            + KEY_REG_URL + " TEXT,"
            + KEY_CONTACT_NAME + " TEXT,"
            + KEY_CONTACT_EMAIL + " TEXT,"
            + KEY_CONTACT_PHONE + " TEXT,"
            + KEY_MAX_PARTICIPANTS + " INTEGER,"
            + KEY_SOCIAL_MEDIA_URL + " TEXT,"
            + KEY_ADDITIONAL_INFO + " TEXT,"
            + KEY_STATUS + " TEXT,"
            + KEY_CREATED_BY + " TEXT,"
            + KEY_CREATED_AT + " INTEGER,"
            + KEY_UPDATED_AT + " INTEGER"
            + ")";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EVENTS);

        // Seed users
        seedUsers(db);

        // Seed sample events
        seedEvents(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedUsers(SQLiteDatabase db) {
        insertUser(db, "admin", "admin123", "ADMIN", "System Administrator");
        insertUser(db, "duet_auth", "duet123", "UNIVERSITY_AUTHORITY", "DUET Registrar Office");
        insertUser(db, "club_auth", "club123", "CLUB_AUTHORITY", "CSE Programming Club");
    }

    private void insertUser(SQLiteDatabase db, String userId, String password, String role, String displayName) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_PASSWORD_HASH, hashPassword(password));
        values.put(KEY_ROLE, role);
        values.put(KEY_DISPLAY_NAME, displayName);
        db.insert(TABLE_USERS, null, values);
    }

    private void seedEvents(SQLiteDatabase db) {
        // Event 1: Ongoing Event (Seeded around current time: 2026-08-09T12:57:47)
        insertEvent(db, 
                "DUET National Programming Contest",
                "Annual programming competition for university students from across Bangladesh. Join us to compete for the championship title!",
                "Club",
                "CSE Programming Club",
                "CSE Dept & Programming Club",
                "https://images.unsplash.com/photo-1515187029135-18ee286d815b?q=80&w=600&auto=format&fit=crop", // placeholder banner
                "2026-08-09",
                "09:00",
                "17:00",
                "CSE Computer Lab 3 & Auditorium",
                true,
                "2026-08-08 23:59", // deadline passed (closed registration)
                "https://example.com/npc-register",
                "Prof. Dr. Raju Ahmed",
                "raju.ahmed@duet.ac.bd",
                "+8801713000001",
                200,
                "https://facebook.com/duet.cse.pc",
                "Participants should bring their ID cards.",
                "club_auth"
        );

        // Event 2: Upcoming Event (Registration open)
        insertEvent(db, 
                "Seminar on Generative AI & Career Prospects",
                "Discover the latest breakthroughs in artificial intelligence and how to prepare for careers in machine learning.",
                "University",
                "",
                "ICT Cell, DUET",
                "https://images.unsplash.com/photo-1591453089816-0fbb971b454c?q=80&w=600&auto=format&fit=crop",
                "2026-08-20",
                "14:00",
                "16:00",
                "Central Auditorium, DUET",
                true,
                "2026-08-19 12:00",
                "https://example.com/ai-seminar-reg",
                "ICT Support Team",
                "ict@duet.ac.bd",
                "+8801711000009",
                500,
                "https://linkedin.com/school/duet-gazipur",
                "Open for all DUET departments.",
                "duet_auth"
        );

        // Event 3: Upcoming Event (No Registration required)
        insertEvent(db, 
                "DUET Cultural Fest 2026",
                "Experience a vibrant showcase of music, drama, dance, and poetry performed by students of DUET.",
                "Club",
                "DUET Cultural Club",
                "Student Welfare Center",
                "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?q=80&w=600&auto=format&fit=crop",
                "2026-08-25",
                "16:30",
                "21:30",
                "Playground Stage, DUET",
                false,
                "",
                "",
                "Club Coordinator",
                "cultural@duet.ac.bd",
                "+8801713000002",
                null,
                "",
                "Entry is free for all students, teachers, and staff members.",
                "club_auth"
        );

        // Event 4: Past / Completed Event
        insertEvent(db, 
                "Workshop on Embedded Systems",
                "Hands-on workshop on microcontroller programming and circuit design using Arduino and STM32.",
                "Club",
                "EEE Robotics Society",
                "EEE Department",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758?q=80&w=600&auto=format&fit=crop",
                "2026-08-01",
                "10:00",
                "15:00",
                "Robotics Lab, Old Building",
                true,
                "2026-07-30 18:00",
                "https://example.com/embedded-reg",
                "Robotics Coordinator",
                "robotics@duet.ac.bd",
                "+8801711000005",
                50,
                "",
                "Workshop kits will be provided.",
                "club_auth"
        );
    }

    private void insertEvent(SQLiteDatabase db, String title, String description, String type, 
                             String clubName, String organizerName, String bannerUrl, String date, 
                             String startTime, String endTime, String venue, boolean regRequired, 
                             String regDeadline, String regUrl, String contactName, String contactEmail, 
                             String contactPhone, Integer maxPart, String socialMedia, String addInfo, 
                             String createdBy) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_TYPE, type);
        values.put(KEY_CLUB_NAME, clubName);
        values.put(KEY_ORGANIZER_NAME, organizerName);
        values.put(KEY_BANNER_URL, bannerUrl);
        values.put(KEY_EVENT_DATE, date);
        values.put(KEY_START_TIME, startTime);
        values.put(KEY_END_TIME, endTime);
        values.put(KEY_VENUE, venue);
        values.put(KEY_REG_REQUIRED, regRequired ? 1 : 0);
        values.put(KEY_REG_DEADLINE, regDeadline);
        values.put(KEY_REG_URL, regUrl);
        values.put(KEY_CONTACT_NAME, contactName);
        values.put(KEY_CONTACT_EMAIL, contactEmail);
        values.put(KEY_CONTACT_PHONE, contactPhone);
        values.put(KEY_MAX_PARTICIPANTS, maxPart);
        values.put(KEY_SOCIAL_MEDIA_URL, socialMedia);
        values.put(KEY_ADDITIONAL_INFO, addInfo);
        values.put(KEY_STATUS, "Active"); // "Active" status, dynamic status calculated in client/adapter
        values.put(KEY_CREATED_BY, createdBy);
        values.put(KEY_CREATED_AT, System.currentTimeMillis());
        values.put(KEY_UPDATED_AT, System.currentTimeMillis());
        db.insert(TABLE_EVENTS, null, values);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e("EventDbHelper", "Error hashing password", e);
            return password; // Fallback
        }
    }
}
